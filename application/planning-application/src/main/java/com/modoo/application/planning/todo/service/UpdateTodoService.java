package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.interim.InterimCancelReminderEvent;
import com.modoo.application.common.dto.interim.InterimSetReminderEvent;
import com.modoo.application.common.dto.todo.request.UpdateTodoReminderRequest;
import com.modoo.application.common.dto.todo.request.UpdateTodoRequest;
import com.modoo.application.common.dto.todo.response.BasicTodoResponse;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.reminder.out.ReminderCommandPort;
import com.modoo.application.common.port.reminder.out.ReminderLoaderPort;
import com.modoo.application.common.port.todo.in.UpdateTodoUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.todo.out.TodoUpdatePort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.planning.todo.service.TodoDomainService;
import com.modoo.domain.user.user.aggregate.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateTodoService implements UpdateTodoUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final TodoUpdatePort todoUpdatePort;
  private final ReminderLoaderPort reminderLoaderPort;
  private final ReminderCommandPort reminderCommandPort;
  private final TodoDomainService todoDomainService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public BasicTodoResponse update(Long loginId, Long todoId, UpdateTodoRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        todoId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(),
        TodoErrorCode.GOAL_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(user.getId());
    goal.validateGoalCreator(user.getId());

    Todo updatedTodo = todoDomainService.update(todo, request.toCommand());
    Todo saved = todoUpdatePort.update(updatedTodo);

    replaceReminders(user.getId(), saved, request.reminders());

    return BasicTodoResponse.from(saved);
  }

  private void replaceReminders(Long userId, Todo todo, List<UpdateTodoReminderRequest> requests) {
    List<Reminder> existingReminders = reminderLoaderPort.getRemindersByTodoId(todo.getId());
    Map<Long, Reminder> existingReminderMap = existingReminders.stream()
        .collect(Collectors.toMap(Reminder::getId, Function.identity()));
    List<UpdateTodoReminderRequest> reminderRequests =
        Objects.nonNull(requests) ? requests : Collections.emptyList();
    Set<Long> requestedReminderIds = reminderRequests.stream()
        .map(UpdateTodoReminderRequest::id)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(HashSet::new));

    reminderRequests.stream()
        .map(request -> upsertReminder(userId, todo, existingReminderMap, request))
        .forEach(savedReminder ->
            applicationEventPublisher.publishEvent(
                InterimSetReminderEvent.from(userId, savedReminder)
            )
        );

    existingReminders.stream()
        .filter(reminder -> !requestedReminderIds.contains(reminder.getId()))
        .forEach(reminder -> {
          reminderCommandPort.deleteById(reminder.getId());
          applicationEventPublisher.publishEvent(InterimCancelReminderEvent.from(userId, reminder));
        });
  }

  private Reminder upsertReminder(
      Long userId,
      Todo todo,
      Map<Long, Reminder> existingReminderMap,
      UpdateTodoReminderRequest request
  ) {
    if (Objects.nonNull(request.id()) && existingReminderMap.containsKey(request.id())) {
      Reminder target = existingReminderMap.get(request.id());
      Reminder updated = target.update(todo.getScheduleDatetime(), request.remindsAt());
      return reminderCommandPort.update(updated);
    }

    Reminder newReminder = Reminder.from(
        userId,
        todo.getId(),
        request.remindsAt(),
        todo.getScheduleDatetime()
    );
    return reminderCommandPort.save(newReminder);
  }

}
