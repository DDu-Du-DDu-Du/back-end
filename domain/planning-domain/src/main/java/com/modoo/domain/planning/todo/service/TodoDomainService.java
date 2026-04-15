package com.modoo.domain.planning.todo.service;

import com.modoo.common.annotation.DomainService;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.planning.todo.dto.CreateTodoCommand;
import com.modoo.domain.planning.todo.dto.UpdateTodoCommand;
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
        .build();
  }

  public Todo update(Todo todo, UpdateTodoCommand command) {
    return todo.update(
        command.goalId(),
        command.name(),
        command.memo(),
        command.scheduledOn(),
        command.beginAt(),
        command.endAt()
    );
  }

}
