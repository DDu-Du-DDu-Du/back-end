package com.ddudu.todo.controller;

import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      @RequestParam(name = "date", required = false)
          String date
  ) {
    try {
      validateDate(date);

      List<TodoListResponse> response = todoService.findDailyTodoList(date);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    } catch (DateTimeParseException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("유효하지 않은 날짜입니다.");
    }
  }

  private void validateDate(String date) {
    if (date != null && !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
      throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형식으로 입력해주세요.");
    }

    if (date != null) {
      LocalDate.parse(date);
    }
  }

}
