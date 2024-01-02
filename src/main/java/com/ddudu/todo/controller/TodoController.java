package com.ddudu.todo.controller;

import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;

  @GetMapping("/{id}")
  public ResponseEntity<TodoResponse> get(
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.findById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<TodoListResponse>> getDaily(
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
          LocalDate date
  ) {
    date = (date == null) ? LocalDate.now() : date;

    List<TodoListResponse> response = todoService.findDailyTodoList(date);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<TodoResponse> updateStatus(
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.updateStatus(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/weekly")
  public ResponseEntity<List<TodoCompletionResponse>> getWeeklyCompletion(
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
          LocalDate date
  ) {
    DayOfWeek weekStart = DayOfWeek.MONDAY;
    date = (date == null) ? LocalDate.now()
        .with(weekStart) : date.with(weekStart);

    List<TodoCompletionResponse> completionList = todoService.findWeeklyTodoCompletion(date);
    return ResponseEntity.ok(completionList);
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<TodoCompletionResponse>> getMonthlyCompletion(
      @RequestParam(value = "date", required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
          YearMonth yearMonth
  ) {
    yearMonth = (yearMonth == null) ? YearMonth.now() : yearMonth;

    List<TodoCompletionResponse> completionList = todoService.findMonthlyTodoCompletion(yearMonth);
    return ResponseEntity.ok(completionList);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("할 일 아이디가 존재하지 않습니다.");
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("유효하지 않은 날짜입니다.");
  }

}
