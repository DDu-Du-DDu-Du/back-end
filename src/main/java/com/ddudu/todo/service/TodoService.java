package com.ddudu.todo.service;

import com.ddudu.common.exception.DataNotFoundException;
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
  public TodoInfo create(Long userId, @Valid CreateTodoRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.USER_NOT_EXISTING));
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

  public TodoResponse findById(Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING));

    return TodoResponse.from(todo);
  }

  public List<TodoListResponse> findAllByDate(Long userId, LocalDate date) {
    User user = findUser(userId);
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

  public List<TodoCompletionResponse> findWeeklyCompletions(Long userId, LocalDate date) {
    User user = findUser(userId);
    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = startDate.plusDays(7);

    return generateCompletions(startDate, endDate, user);
  }

  public List<TodoCompletionResponse> findMonthlyCompletions(Long userId, YearMonth yearMonth) {
    User user = findUser(userId);
    LocalDateTime startDate = yearMonth.atDay(1)
        .atStartOfDay();
    LocalDateTime endDate = startDate.plusMonths(1);

    return generateCompletions(startDate, endDate, user);
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

  @Transactional
  public TodoResponse updateStatus(Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.ID_NOT_EXISTING));
    todo.switchStatus();

    return TodoResponse.from(todo);
  }

  private User findUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException(TodoErrorCode.USER_NOT_EXISTING));
  }

}
