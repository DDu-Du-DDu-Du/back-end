package com.ddudu.todo.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.dto.request.CreateTodoRequest;
import com.ddudu.todo.dto.request.UpdateTodoRequest;
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

  public List<TodoListResponse> findAllByDate(Long loginId, LocalDate date) {
    User user = findUser(loginId);
    List<Goal> goals = goalRepository.findAllByUser(user);
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
  public TodoInfo update(Long loginId, Long id, UpdateTodoRequest request) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING));

    checkPermission(loginId, todo);

    Goal goal = goalRepository.findById(request.goalId())
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.GOAL_NOT_EXISTING));

    checkGoalPermission(loginId, goal);

    todo.applyTodoUpdates(goal, request.name(), request.beginAt());

    return TodoInfo.from(todo);
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

  @Transactional
  public void delete(Long loginId, Long id) {
    todoRepository.findById(id)
        .ifPresent(todo -> {
          checkPermission(loginId, todo);
          todo.delete();
        });
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

  private void checkPermission(Long userId, Todo todo) {
    if (!todo.isCreatedByUser(userId)) {
      throw new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY);
    }
  }

  private void checkGoalPermission(Long userId, Goal goal) {
    if (!goal.isCreatedByUser(userId)) {
      throw new ForbiddenException(TodoErrorCode.INVALID_AUTHORITY);
    }
  }

}
