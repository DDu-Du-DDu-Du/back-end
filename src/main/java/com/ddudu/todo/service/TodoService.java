package com.ddudu.todo.service;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.dto.request.CreateTodoRequest;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.repository.TodoRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
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
        .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다."));
    Goal goal = goalRepository.findById(request.goalId())
        .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 목표가 존재하지 않습니다."));

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
        .orElseThrow(() -> new EntityNotFoundException("할 일 아이디가 존재하지 않습니다."));

    return TodoResponse.from(todo);
  }

  public List<TodoListResponse> findDailyTodoList(LocalDate date) {
    List<Goal> goals = goalRepository.findAll();
    List<Todo> todos = todoRepository.findTodosByDate(
        date.atStartOfDay(), date.atTime(LocalTime.MAX)
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

}
