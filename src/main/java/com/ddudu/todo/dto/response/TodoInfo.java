package com.ddudu.todo.dto.response;

import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.domain.TodoStatus;
import lombok.Builder;

@Builder
public record TodoInfo(
    Long id,
    String name,
    TodoStatus status
) {

  public static TodoInfo from(Todo todo) {
    return TodoInfo.builder()
        .id(todo.getId())
        .name(todo.getName())
        .status(todo.getStatus())
        .build();
  }

}
