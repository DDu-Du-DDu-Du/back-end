package com.ddudu.todo.controller;

import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getTodo(
      @PathVariable
          Long id
  ) {
    try {
      TodoResponse response = todoService.findById(id);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<?> getDailyTodoList(
      LocalDate date
  ) {
    date = (date == null) ? LocalDate.now() : date;

    List<TodoListResponse> response = todoService.findDailyTodoList(date);
    return ResponseEntity.ok(response);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("유효하지 않은 날짜입니다.");
  }

}
