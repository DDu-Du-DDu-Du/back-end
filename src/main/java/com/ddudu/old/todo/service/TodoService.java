package com.ddudu.old.todo.service;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.goal.domain.Goal;
import com.ddudu.old.goal.domain.GoalRepository;
import com.ddudu.old.goal.domain.PrivacyType;
import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.todo.domain.Todo;
import com.ddudu.old.todo.domain.TodoRepository;
import com.ddudu.old.todo.dto.request.CreateTodoRequest;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.LikeInfo;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoInfo;
import com.ddudu.old.todo.dto.response.TodoListResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import com.ddudu.old.todo.exception.TodoErrorCode;
import com.ddudu.old.user.domain.FollowingRepository;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.application.exception.ErrorCode;
import com.ddudu.presentation.api.exception.ForbiddenException;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class TodoService {

  private final TodoRepository todoRepository;
  private final GoalRepository goalRepository;
  private final UserRepository userRepository;
  private final FollowingRepository followingRepository;
  private final LikeRepository likeRepository;

  @Transactional
  public TodoInfo create(
      Long loginId,
      @Valid
      CreateTodoRequest request
  ) {
    User user = findUser(loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING);
    Goal goal = findGoal(request.goalId(), TodoErrorCode.GOAL_NOT_EXISTING);

    checkGoalPermission(loginId, goal);

    Todo todo = Todo.builder()
        .name(request.name())
        .goal(goal)
        .user(user)
        .beginAt(request.beginAt())
        .build();

    return TodoInfo.from(todoRepository.save(todo));
  }

  public TodoResponse findById(Long loginId, Long id) {
    Todo todo = findTodo(id, TodoErrorCode.ID_NOT_EXISTING);

    checkPermission(loginId, todo);

    return TodoResponse.from(todo);
  }

  public List<TodoListResponse> findAllByDate(Long loginId, Long userId, LocalDate date) {
    User loginUser = findUser(loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING);
    User user = determineUser(loginId, userId, loginUser);

    List<Goal> goals = goalRepository.findAllByUserAndPrivacyTypes(
        user, determinePrivacyTypes(loginUser, user));

    List<Todo> todos = todoRepository.findTodosByDate(
        date.atStartOfDay(), date.atTime(LocalTime.MAX), user
    );

    Map<Long, List<Todo>> todosByGoal = todos.stream()
        .collect(Collectors.groupingBy(todo -> todo.getGoal()
            .getId()));

    Map<Long, List<Like>> likesByTodo = likeRepository.findByTodos(todos)
        .stream()
        .collect(Collectors.groupingBy(like -> like.getTodo()
            .getId()));

    return goals.stream()
        .map(goal -> mapGoalToTodoListResponse(goal, todosByGoal, likesByTodo))
        .toList();
  }

  public List<TodoCompletionResponse> findWeeklyCompletions(
      Long loginId, Long userId, LocalDate date
  ) {
    User loginUser = findUser(loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING);
    User user = determineUser(loginId, userId, loginUser);

    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = startDate.plusDays(7);

    return generateCompletions(startDate, endDate, loginUser, user);
  }

  public List<TodoCompletionResponse> findMonthlyCompletions(
      Long loginId, Long userId, YearMonth yearMonth
  ) {
    User loginUser = findUser(loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING);
    User user = determineUser(loginId, userId, loginUser);

    LocalDateTime startDate = yearMonth.atDay(1)
        .atStartOfDay();
    LocalDateTime endDate = startDate.plusMonths(1);

    return generateCompletions(startDate, endDate, loginUser, user);
  }

  @Transactional
  public TodoInfo update(Long loginId, Long id, UpdateTodoRequest request) {
    Todo todo = findTodo(id, TodoErrorCode.ID_NOT_EXISTING);

    checkPermission(loginId, todo);

    Goal goal = findGoal(request.goalId(), TodoErrorCode.GOAL_NOT_EXISTING);

    checkGoalPermission(loginId, goal);

    todo.applyTodoUpdates(goal, request.name(), request.beginAt());

    todoRepository.update(todo);

    return TodoInfo.from(todo);
  }

  @Transactional
  public void updateStatus(Long loginId, Long id) {
    Todo todo = findTodo(id, TodoErrorCode.ID_NOT_EXISTING);

    checkPermission(loginId, todo);

    todo.switchStatus();

    todoRepository.update(todo);
  }

  @Transactional
  public void delete(Long loginId, Long id) {
    todoRepository.findById(id)
        .ifPresent(todo -> {
          checkPermission(loginId, todo);
          todoRepository.delete(todo);
        });
  }

  private List<TodoCompletionResponse> generateCompletions(
      LocalDateTime startDate, LocalDateTime endDate, User loginUser, User user
  ) {
    List<PrivacyType> privacyTypes = determinePrivacyTypes(loginUser, user);

    Map<LocalDate, TodoCompletionResponse> completionByDate = todoRepository.findTodosCompletion(
            startDate, endDate, user, privacyTypes)
        .stream()
        .collect(Collectors.toMap(TodoCompletionResponse::date, response -> response));

    List<TodoCompletionResponse> completionList = new ArrayList<>();
    for (LocalDateTime currentDate = startDate; currentDate.isBefore(endDate);
        currentDate = currentDate.plusDays(1)) {
      TodoCompletionResponse response = completionByDate.getOrDefault(
          currentDate.toLocalDate(),
          TodoCompletionResponse.createEmptyResponse(currentDate.toLocalDate())
      );
      completionList.add(response);
    }

    return completionList;
  }

  private User determineUser(Long loginId, Long userId, User loginUser) {
    if (loginId.equals(userId)) {
      return loginUser;
    }

    return findUser(userId, TodoErrorCode.USER_NOT_EXISTING);
  }

  private User findUser(Long userId, ErrorCode errorCode) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private Goal findGoal(Long goalId, ErrorCode errorCode) {
    return goalRepository.findById(goalId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private Todo findTodo(Long todoId, ErrorCode errorCode) {
    return todoRepository.findById(todoId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private void checkPermission(Long loginId, Todo todo) {
    if (!todo.isCreatedByUser(loginId)) {
      throw new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY);
    }
  }

  private void checkGoalPermission(Long userId, Goal goal) {
    if (!goal.isCreatedByUser(userId)) {
      throw new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY);
    }
  }

  private List<PrivacyType> determinePrivacyTypes(User loginUser, User user) {
    if (loginUser.equals(user)) {
      return List.of(PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC);
    }

    if (followingRepository.existsByFollowerAndFollowee(loginUser, user)) {
      return List.of(PrivacyType.FOLLOWER, PrivacyType.PUBLIC);
    }

    return List.of(PrivacyType.PUBLIC);
  }

  private TodoListResponse mapGoalToTodoListResponse(
      Goal goal, Map<Long, List<Todo>> todosByGoal, Map<Long, List<Like>> likesByTodo
  ) {
    List<TodoInfo> todoInfos = todosByGoal.getOrDefault(goal.getId(), Collections.emptyList())
        .stream()
        .map(todo -> mapTodoToTodoInfoWithLikes(todo, likesByTodo))
        .toList();

    return TodoListResponse.from(goal, todoInfos);
  }

  private TodoInfo mapTodoToTodoInfoWithLikes(Todo todo, Map<Long, List<Like>> likesByTodo) {
    List<Long> likedUsers = likesByTodo.getOrDefault(todo.getId(), Collections.emptyList())
        .stream()
        .map(like -> like.getUser()
            .getId())
        .toList();

    return TodoInfo.from(todo, LikeInfo.from(likedUsers));
  }

}
