package com.ddudu.todo.controller;

import com.ddudu.todo.dto.request.CreateTodoRequest;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
  public ResponseEntity<?> getTodo(
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.findById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<?> getDailyTodoList(
      LocalDate date
  ) {
    date = (date == null) ? LocalDate.now() : date;

    List<TodoListResponse> response = todoService.findDailyTodoList(date);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<?> updateTodoStatus(
      @PathVariable
          Long id
  ) {
    TodoResponse response = todoService.updateStatus(id);
    return ResponseEntity.ok(response);
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
