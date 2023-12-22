package com.ddudu.todo.controller;

import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    try {
      TodoResponse response = todoService.findById(id);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(e.getMessage());
    }
  }

}
