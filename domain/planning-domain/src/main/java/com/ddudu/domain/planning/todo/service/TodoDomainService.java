package com.ddudu.domain.planning.todo.service;

import com.ddudu.common.annotation.DomainService;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.dto.CreateTodoCommand;
import com.ddudu.domain.planning.todo.dto.UpdateTodoCommand;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class TodoDomainService {

  public Todo create(Long userId, CreateTodoCommand command) {
    return Todo.builder()
        .userId(userId)
        .goalId(command.goalId())
        .name(command.name())
        .memo(command.memo())
        .scheduledOn(command.scheduledOn())
        .beginAt(command.beginAt())
        .endAt(command.endAt())
        .remindDays(command.remindDays())
        .remindHours(command.remindHours())
        .remindMinutes(command.remindMinutes())
        .build();
  }

  public Todo update(Todo todo, UpdateTodoCommand command) {
    return todo.update(
        command.goalId(),
        command.name(),
        command.memo(),
        command.scheduledOn(),
        command.beginAt(),
        command.endAt(),
        command.remindDays(),
        command.remindHours(),
        command.remindMinutes()
    );
  }

}
