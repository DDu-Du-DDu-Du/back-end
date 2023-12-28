package com.ddudu.todo.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TodoCompletionResponse(
    String date,
    int totalTodos,
    int uncompletedTodos
) {

  public static TodoCompletionResponse createEmptyResponse(LocalDate date) {
    return TodoCompletionResponse.builder()
        .date(date.toString())
        .totalTodos(0)
        .uncompletedTodos(0)
        .build();
  }

}
