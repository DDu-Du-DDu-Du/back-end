package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.todo.request.SetReminderRequest;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.ddudu.application.common.port.todo.in.SetReminderUseCase;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoUpdatePort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@UseCase
@RequiredArgsConstructor
public class SetReminderService implements SetReminderUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final TodoUpdatePort todoUpdatePort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public void setReminder(Long loginId, Long id, SetReminderRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        id,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(user.getId());

    Todo todoWithReminder = todo.setReminder(request.days(), request.hours(), request.minutes());
    Todo updated = todoUpdatePort.update(todoWithReminder);
    InterimSetReminderEvent interimEvent = InterimSetReminderEvent.from(
        user.getId(),
        updated
    );

    applicationEventPublisher.publishEvent(interimEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void publishNotificationEventAfterCommit(InterimSetReminderEvent event) {
    NotificationEventSaveEvent saveEvent = NotificationEventSaveEvent.from(event);

    applicationEventPublisher.publishEvent(saveEvent);
  }

}
