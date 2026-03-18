package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimDeleteTodoEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.port.ddudu.in.DeleteTodoUseCase;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DeleteTodoPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@UseCase
@RequiredArgsConstructor
public class DeleteTodoService implements DeleteTodoUseCase {

  private final TodoLoaderPort dduduLoaderPort;
  private final DeleteTodoPort deleteTodoPort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public void delete(Long loginId, Long dduduId) {
    Optional<Todo> optionalTodo = dduduLoaderPort.getOptionalTodo(dduduId);

    if (optionalTodo.isEmpty()) {
      return;
    }

    Todo ddudu = optionalTodo.get();

    ddudu.validateTodoCreator(loginId);
    deleteTodoPort.delete(ddudu);

    InterimCancelReminderEvent interimEvent = InterimCancelReminderEvent.from(
        loginId,
        ddudu
    );

    applicationEventPublisher.publishEvent(interimEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void publishNotificationEventAfterCommit(InterimDeleteTodoEvent event) {
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.from(event);

    applicationEventPublisher.publishEvent(removeEvent);
  }

}
