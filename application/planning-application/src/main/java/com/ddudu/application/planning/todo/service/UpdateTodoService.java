package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.todo.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.port.todo.in.UpdateTodoUseCase;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoUpdatePort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.service.TodoDomainService;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
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

    if (todo.hasReminder()) {
      InterimCancelReminderEvent cancelEvent = InterimCancelReminderEvent.from(user.getId(), todo);
      applicationEventPublisher.publishEvent(cancelEvent);
    }

    if (saved.hasReminder()) {
      InterimSetReminderEvent setEvent = InterimSetReminderEvent.from(user.getId(), saved);
      applicationEventPublisher.publishEvent(setEvent);
    }

    return BasicTodoResponse.from(saved);
  }

}
