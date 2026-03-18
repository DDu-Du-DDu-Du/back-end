package com.ddudu.application.common.dto.todo.response;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BasicTodoResponse(
    Long id,
    String name,
    TodoStatus status,
    LocalDateTime postponedAt
) {

  public static BasicTodoResponse from(Todo todo) {
    return BasicTodoResponse.builder()
        .id(todo.getId())
        .name(todo.getName())
        .status(todo.getStatus())
        .postponedAt(todo.getPostponedAt())
        .build();
  }

}
