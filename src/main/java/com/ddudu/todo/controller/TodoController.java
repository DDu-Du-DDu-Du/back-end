package com.ddudu.todo.controller;

import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
