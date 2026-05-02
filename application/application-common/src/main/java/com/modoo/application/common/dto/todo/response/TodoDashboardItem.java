package com.modoo.application.common.dto.todo.response;

import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.planning.todo.aggregate.enums.TodoStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TodoDashboardItem(
    Long id,
    String name,
    LocalDate scheduledOn,
    LocalTime beginAt,
    LocalTime endAt,
    TodoStatus status,
    LocalDateTime postponedAt
) {

  public static TodoDashboardItem from(Todo todo) {
    return new TodoDashboardItem(
        todo.getId(),
        todo.getName(),
        todo.getScheduledOn(),
        todo.getBeginAt(),
        todo.getEndAt(),
        todo.getStatus(),
        todo.getPostponedAt()
    );
  }

}
