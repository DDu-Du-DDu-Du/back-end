package com.ddudu.todo.dto.response;

import com.ddudu.todo.domain.Todo;
import lombok.Builder;

@Builder
public record TodoInfo(
    Long id,
    String name,
    String status
) {

  public static TodoInfo from(Todo todo) {
    return TodoInfo.builder()
        .id(todo.getId())
        .name(todo.getName())
        .status(todo.getStatus()
            .name())
        .build();
  }

}
