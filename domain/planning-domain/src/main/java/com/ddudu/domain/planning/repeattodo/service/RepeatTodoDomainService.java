package com.ddudu.domain.planning.repeattodo.service;

import com.ddudu.common.annotation.DomainService;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeattodo.aggregate.vo.RepeatPattern;
import com.ddudu.domain.planning.repeattodo.dto.CreateRepeatTodoCommand;
import com.ddudu.domain.planning.repeattodo.dto.UpdateRepeatTodoCommand;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class RepeatTodoDomainService {

  public RepeatTodo create(Long goalId, CreateRepeatTodoCommand command) {
    RepeatType repeatType = RepeatType.from(command.repeatType());
    RepeatPattern repeatPattern = repeatType.createRepeatPattern(
        command.repeatDaysOfWeek(),
        command.repeatDaysOfMonth(),
        command.lastDayOfMonth()
    );
    Long goalIdFinal = Objects.nonNull(goalId) ? goalId : command.goalId();

    return RepeatTodo.builder()
        .name(command.name())
        .goalId(goalIdFinal)
        .startDate(command.startDate())
        .endDate(command.endDate())
        .repeatType(repeatType)
        .repeatPattern(repeatPattern)
        .beginAt(command.beginAt())
        .endAt(command.endAt())
        .build();
  }

  public List<Todo> createRepeatedTodos(Long userId, RepeatTodo repeatTodo) {
    return repeatTodo.getRepeatDates()
        .stream()
        .map(date -> Todo.builder()
            .name(repeatTodo.getName())
            .goalId(repeatTodo.getGoalId())
            .userId(userId)
            .repeatTodoId(repeatTodo.getId())
            .scheduledOn(date)
            .beginAt(repeatTodo.getBeginAt())
            .endAt(repeatTodo.getEndAt())
            .build()
        )
        .toList();
  }

  public List<Todo> createRepeatedTodosAfter(
      Long userId,
      RepeatTodo repeatTodo,
      LocalDateTime now
  ) {
    return repeatTodo.getRepeatDates()
        .stream()
        .filter(date -> date.isAfter(ChronoLocalDate.from(now)))
        .map(date -> Todo.builder()
            .name(repeatTodo.getName())
            .goalId(repeatTodo.getGoalId())
            .userId(userId)
            .repeatTodoId(repeatTodo.getId())
            .scheduledOn(date)
            .beginAt(repeatTodo.getBeginAt())
            .endAt(repeatTodo.getEndAt())
            .build()
        )
        .toList();
  }

  public RepeatTodo update(RepeatTodo repeatTodo, UpdateRepeatTodoCommand command) {
    RepeatType repeatType = RepeatType.from(command.repeatType());
    RepeatPattern repeatPattern = repeatType.createRepeatPattern(
        command.repeatDaysOfWeek(),
        command.repeatDaysOfMonth(), command.lastDayOfMonth()
    );

    return repeatTodo.update(
        command.name(),
        repeatType,
        repeatPattern,
        command.startDate(),
        command.endDate(),
        command.beginAt(),
        command.endAt()
    );
  }

}
