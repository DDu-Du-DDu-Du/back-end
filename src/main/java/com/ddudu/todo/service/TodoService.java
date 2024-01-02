package com.ddudu.todo.service;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.repository.TodoRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

  private final TodoRepository todoRepository;
  private final GoalRepository goalRepository;
  private final UserRepository userRepository;

  public TodoResponse findById(Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("할 일 아이디가 존재하지 않습니다."));

    return TodoResponse.from(todo);
  }

  public List<TodoListResponse> findDailyTodoList(Long userId, LocalDate date) {
    User user = findUser(userId);
    List<Goal> goals = goalRepository.findAll();
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

  @Transactional
  public TodoResponse updateStatus(Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("할 일 아이디가 존재하지 않습니다."));
    todo.switchStatus();

    return TodoResponse.from(todo);
  }

  public List<TodoCompletionResponse> findWeeklyTodoCompletion(LocalDate date) {
    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = startDate.plusDays(7);

    return generateCompletions(startDate, endDate);
  }

  public List<TodoCompletionResponse> findMonthlyTodoCompletion(YearMonth yearMonth) {
    LocalDateTime startDate = yearMonth.atDay(1)
        .atStartOfDay();
    LocalDateTime endDate = startDate.plusMonths(1);

    return generateCompletions(startDate, endDate);
  }

  private List<TodoCompletionResponse> generateCompletions(
      LocalDateTime startDate, LocalDateTime endDate
  ) {
    Map<LocalDate, TodoCompletionResponse> completionByDate = todoRepository.findTodosCompletion(
            startDate, endDate)
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
        .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
  }

}
