package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.port.todo.in.CancelReminderUseCase;
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
public class CancelReminderService implements CancelReminderUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final TodoUpdatePort todoUpdatePort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public void cancel(Long loginId, Long id) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        id,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(user.getId());

    Todo todoWithoutReminder = todo.cancelReminder();
    Todo updated = todoUpdatePort.update(todoWithoutReminder);

    InterimCancelReminderEvent interimEvent = InterimCancelReminderEvent.from(
        user.getId(),
        updated
    );

    applicationEventPublisher.publishEvent(interimEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void publishNotificationEventAfterCommit(InterimCancelReminderEvent event) {
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.from(event);

    applicationEventPublisher.publishEvent(removeEvent);
  }

}
