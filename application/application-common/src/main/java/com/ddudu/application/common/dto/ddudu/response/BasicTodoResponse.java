package com.ddudu.application.common.dto.ddudu.response;

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

  public static BasicTodoResponse from(Todo ddudu) {
    return BasicTodoResponse.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .postponedAt(ddudu.getPostponedAt())
        .build();
  }

}
