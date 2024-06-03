package com.ddudu.old.todo.service;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.exception.ErrorCode;
import com.ddudu.old.goal.domain.OldGoalRepository;
import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.todo.domain.OldTodoRepository;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.LikeInfo;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import com.ddudu.old.user.domain.FollowingRepository;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
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

  private final OldTodoRepository oldTodoRepository;
  private final OldGoalRepository oldGoalRepository;
  private final UserRepository userRepository;
  private final FollowingRepository followingRepository;
  private final LikeRepository likeRepository;

  public TodoResponse findById(Long loginId, Long id) {
    Ddudu ddudu = findTodo(id, DduduErrorCode.ID_NOT_EXISTING);

    return TodoResponse.from(ddudu);
  }

  public List<GoalGroupedDdudus> findAllByDateGroupedByGoal(
      Long loginId, Long userId, LocalDate date
  ) {
    User loginUser = findUser(loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING);
    User user = determineUser(loginId, userId, loginUser);

    List<Goal> goals = oldGoalRepository.findAllByUserAndPrivacyTypes(
        user, determinePrivacyTypes(loginUser, user));

    List<Ddudu> ddudus = oldTodoRepository.findTodosByDate(
        date.atStartOfDay(), date.atTime(LocalTime.MAX), user
    );

    Map<Long, List<Ddudu>> todosByGoal = ddudus.stream()
        .collect(Collectors.groupingBy(todo -> todo.getGoalId()));

    Map<Long, List<Like>> likesByTodo = likeRepository.findByTodos(ddudus)
        .stream()
        .collect(Collectors.groupingBy(like -> like.getDdudu()
            .getId()));

    return goals.stream()
        .map(goal -> mapGoalToTodoListResponse(goal, todosByGoal, likesByTodo))
        .toList();
  }

  public List<TodoCompletionResponse> findWeeklyCompletions(
      Long loginId, Long userId, LocalDate date
  ) {
    User loginUser = findUser(loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING);
    User user = determineUser(loginId, userId, loginUser);

    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = startDate.plusDays(7);

    return generateCompletions(startDate, endDate, loginUser, user);
  }

  public List<TodoCompletionResponse> findMonthlyCompletions(
      Long loginId, Long userId, YearMonth yearMonth
  ) {
    User loginUser = findUser(loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING);
    User user = determineUser(loginId, userId, loginUser);

    LocalDateTime startDate = yearMonth.atDay(1)
        .atStartOfDay();
    LocalDateTime endDate = startDate.plusMonths(1);

    return generateCompletions(startDate, endDate, loginUser, user);
  }

  @Transactional
  public BasicDduduResponse update(Long loginId, Long id, UpdateTodoRequest request) {
    Ddudu ddudu = findTodo(id, DduduErrorCode.ID_NOT_EXISTING);

    Goal goal = findGoal(request.goalId(), DduduErrorCode.GOAL_NOT_EXISTING);

    checkGoalPermission(loginId, goal);

    ddudu.applyTodoUpdates(goal, request.name(), request.beginAt());

    oldTodoRepository.update(ddudu);

    return BasicDduduResponse.from(ddudu);
  }

  @Transactional
  public void updateStatus(Long loginId, Long id) {
    Ddudu ddudu = findTodo(id, DduduErrorCode.ID_NOT_EXISTING);

    ddudu.switchStatus();

    oldTodoRepository.update(ddudu);
  }

  @Transactional
  public void delete(Long loginId, Long id) {
    oldTodoRepository.findById(id)
        .ifPresent(todo -> {
          oldTodoRepository.delete(todo);
        });
  }

  private List<TodoCompletionResponse> generateCompletions(
      LocalDateTime startDate, LocalDateTime endDate, User loginUser, User user
  ) {
    List<PrivacyType> privacyTypes = determinePrivacyTypes(loginUser, user);

    Map<LocalDate, TodoCompletionResponse> completionByDate = oldTodoRepository.findTodosCompletion(
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

    return findUser(userId, DduduErrorCode.USER_NOT_EXISTING);
  }

  private User findUser(Long userId, ErrorCode errorCode) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private Goal findGoal(Long goalId, ErrorCode errorCode) {
    return oldGoalRepository.findById(goalId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private Ddudu findTodo(Long todoId, ErrorCode errorCode) {
    return oldTodoRepository.findById(todoId)
        .orElseThrow(() -> new DataNotFoundException(errorCode));
  }

  private void checkGoalPermission(Long userId, Goal goal) {
    if (!goal.isCreatedBy(userId)) {
      throw new ForbiddenException(DduduErrorCode.INVALID_AUTHORITY);
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

  private GoalGroupedDdudus mapGoalToTodoListResponse(
      Goal goal, Map<Long, List<Ddudu>> todosByGoal, Map<Long, List<Like>> likesByTodo
  ) {
    List<BasicDduduResponse> basicDduduResponses = todosByGoal.getOrDefault(
            goal.getId(), Collections.emptyList())
        .stream()
        .map(todo -> mapTodoToTodoInfoWithLikes(todo, likesByTodo))
        .toList();

    return GoalGroupedDdudus.of(goal, basicDduduResponses);
  }

  private BasicDduduResponse mapTodoToTodoInfoWithLikes(
      Ddudu ddudu, Map<Long, List<Like>> likesByTodo
  ) {
    List<Long> likedUsers = likesByTodo.getOrDefault(ddudu.getId(), Collections.emptyList())
        .stream()
        .map(like -> like.getUser()
            .getId())
        .toList();

    return BasicDduduResponse.from(ddudu, LikeInfo.from(likedUsers));
  }

}
