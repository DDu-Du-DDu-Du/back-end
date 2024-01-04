package com.ddudu.todo.controller;

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
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.findById(id);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<TodoListResponse>> getDaily(
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
          LocalDate date
  ) {
    date = (date == null) ? LocalDate.now() : date;
    List<TodoListResponse> response = todoService.findDailyTodoList(userId, date);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/weekly")
  public ResponseEntity<List<TodoCompletionResponse>> getWeeklyCompletion(
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
          LocalDate date
  ) {
    DayOfWeek weekStart = DayOfWeek.MONDAY;
    date = (date == null) ? LocalDate.now()
        .with(weekStart) : date.with(weekStart);
    List<TodoCompletionResponse> completionList = todoService.findWeeklyTodoCompletion(
        userId, date);

    return ResponseEntity.ok(completionList);
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<TodoCompletionResponse>> getMonthlyCompletion(
      Long userId,
      @RequestParam(value = "date", required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
          YearMonth yearMonth
  ) {
    yearMonth = (yearMonth == null) ? YearMonth.now() : yearMonth;
    List<TodoCompletionResponse> completionList = todoService.findMonthlyTodoCompletion(
        userId, yearMonth);

    return ResponseEntity.ok(completionList);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<TodoResponse> updateStatus(
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.updateStatus(id);

    return ResponseEntity.ok(response);
  }

}
