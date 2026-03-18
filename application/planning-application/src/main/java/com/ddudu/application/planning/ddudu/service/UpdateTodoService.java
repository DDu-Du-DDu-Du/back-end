package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;
import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.port.ddudu.in.UpdateTodoUseCase;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.application.common.port.ddudu.out.TodoUpdatePort;
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
  private final TodoLoaderPort dduduLoaderPort;
  private final TodoUpdatePort dduduUpdatePort;
  private final TodoDomainService dduduDomainService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public BasicTodoResponse update(Long loginId, Long dduduId, UpdateTodoRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Todo ddudu = dduduLoaderPort.getTodoOrElseThrow(
        dduduId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(),
        TodoErrorCode.GOAL_NOT_EXISTING.getCodeName()
    );

    ddudu.validateTodoCreator(user.getId());
    goal.validateGoalCreator(user.getId());

    Todo updatedTodo = dduduDomainService.update(ddudu, request.toCommand());
    Todo saved = dduduUpdatePort.update(updatedTodo);

    if (ddudu.hasReminder()) {
      InterimCancelReminderEvent cancelEvent = InterimCancelReminderEvent.from(user.getId(), ddudu);
      applicationEventPublisher.publishEvent(cancelEvent);
    }

    if (saved.hasReminder()) {
      InterimSetReminderEvent setEvent = InterimSetReminderEvent.from(user.getId(), saved);
      applicationEventPublisher.publishEvent(setEvent);
    }

    return BasicTodoResponse.from(saved);
  }

}
