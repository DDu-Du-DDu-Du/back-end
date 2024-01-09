package com.ddudu.todo.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.following.repository.FollowingRepository;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.dto.request.CreateTodoRequest;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.exception.TodoErrorCode;
import com.ddudu.todo.repository.TodoRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
@Slf4j
public class TodoService {

  private final TodoRepository todoRepository;
  private final GoalRepository goalRepository;
  private final UserRepository userRepository;
  private final FollowingRepository followingRepository;

  @Transactional
  public TodoInfo create(Long loginId, @Valid CreateTodoRequest request) {
    User user = findUser(loginId);
    Goal goal = goalRepository.findById(request.goalId())
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.GOAL_NOT_EXISTING));

    Todo todo = Todo.builder()
        .name(request.name())
        .goal(goal)
        .user(user)
        .beginAt(request.beginAt())
        .build();

    return TodoInfo.from(todoRepository.save(todo));
  }

  public TodoResponse findById(Long loginId, Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING));

    if (!todo.isCreatedByUser(loginId)) {
      throw new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY);
    }

    return TodoResponse.from(todo);
  }

  public List<TodoListResponse> findAllByDate(Long loginId, Long userId, LocalDate date) {
    User loginUser = findUser(loginId);
    User user = findUser(userId);
    List<Goal> goals;

    log.info("loginId: {}, userId: {}", loginId, userId);

    if (loginUser.equals(user)) {
      log.info("본인 조회");
      goals = goalRepository.findAllByUserAndPrivacyType(user, "ME");
    } else if (followingRepository.existsByFollowerAndFollowee(loginUser, user)
        || followingRepository.existsByFollowerAndFollowee(user, loginUser)) {
      log.info("팔로워 조회");
      goals = goalRepository.findAllByUserAndPrivacyType(user, "FOLLOWER");
    } else {
      log.info("다른 사용자 조회");
      goals = goalRepository.findAllByUserAndPrivacyType(user, "PUBLIC");
    }

    List<Todo> todos = todoRepository.findTodosByDate(
        date.atStartOfDay(), date.atTime(LocalTime.MAX), user
    );

    Map<Long, List<Todo>> todosByGoal = todos.stream()
        .collect(Collectors.groupingBy(todo -> todo.getGoal()
            .getId()));

    return goals.stream()
        .map(goal -> {
          List<TodoInfo> todoInfos = todosByGoal.getOrDefault(goal.getId(), Collections.emptyList())
              .stream()
              .map(TodoInfo::from)
              .toList();

          return TodoListResponse.from(goal, todoInfos);
        })
        .toList();
  }

  public List<TodoCompletionResponse> findWeeklyCompletions(Long loginId, LocalDate date) {
    User user = findUser(loginId);
    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = startDate.plusDays(7);

    return generateCompletions(startDate, endDate, user);
  }

  public List<TodoCompletionResponse> findMonthlyCompletions(Long loginId, YearMonth yearMonth) {
    User user = findUser(loginId);
    LocalDateTime startDate = yearMonth.atDay(1)
        .atStartOfDay();
    LocalDateTime endDate = startDate.plusMonths(1);

    return generateCompletions(startDate, endDate, user);
  }

  @Transactional
  public void delete(Long loginId, Long id) {
    Optional<Todo> optionalTodo = todoRepository.findById(id);

    if (optionalTodo.isEmpty()) {
      return;
    }

    Todo todo = optionalTodo.get();

    if (!todo.isCreatedByUser(loginId)) {
      throw new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY);
    }

    todo.delete();
  }

  @Transactional
  public TodoResponse updateStatus(Long loginId, Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING));

    if (!todo.isCreatedByUser(loginId)) {
      throw new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY);
    }

    todo.switchStatus();

    return TodoResponse.from(todo);
  }

  private List<TodoCompletionResponse> generateCompletions(
      LocalDateTime startDate, LocalDateTime endDate, User user
  ) {
    Map<LocalDate, TodoCompletionResponse> completionByDate = todoRepository.findTodosCompletion(
            startDate, endDate, user)
        .stream()
        .collect(
            Collectors.toMap(TodoCompletionResponse::date, response -> response));

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

  private User findUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.USER_NOT_EXISTING));
  }

}
