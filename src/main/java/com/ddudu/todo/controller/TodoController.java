package com.ddudu.todo.controller;

import com.ddudu.common.annotation.Login;
import com.ddudu.todo.dto.request.CreateTodoRequest;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;

  @PostMapping
  public ResponseEntity<TodoInfo> create(
      @Login
          Long userId,
      @RequestBody
      @Valid
          CreateTodoRequest request
  ) {
    TodoInfo response = todoService.create(userId, request);
    URI uri = URI.create("/api/todos/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TodoResponse> getById(
      @Login
          Long userId,
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.findById(userId, id);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<TodoListResponse>> getDaily(
      @Login
          Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
          LocalDate date
  ) {
    date = (date == null) ? LocalDate.now() : date;
    List<TodoListResponse> response = todoService.findAllByDate(userId, date);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/weekly")
  public ResponseEntity<List<TodoCompletionResponse>> getWeeklyCompletion(
      @Login
          Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
          LocalDate date
  ) {
    DayOfWeek weekStart = DayOfWeek.MONDAY;
    date = (date == null) ? LocalDate.now()
        .with(weekStart) : date.with(weekStart);
    List<TodoCompletionResponse> completionList = todoService.findWeeklyCompletions(
        userId, date);

    return ResponseEntity.ok(completionList);
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<TodoCompletionResponse>> getMonthlyCompletion(
      @Login
          Long userId,
      @RequestParam(value = "date", required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
          YearMonth yearMonth
  ) {
    yearMonth = (yearMonth == null) ? YearMonth.now() : yearMonth;
    List<TodoCompletionResponse> completionList = todoService.findMonthlyCompletions(
        userId, yearMonth);

    return ResponseEntity.ok(completionList);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<TodoResponse> updateStatus(
      @Login
          Long userId,
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.updateStatus(userId, id);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Login
          Long userId,
      @PathVariable
          Long id
  ) {
    todoService.delete(userId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
