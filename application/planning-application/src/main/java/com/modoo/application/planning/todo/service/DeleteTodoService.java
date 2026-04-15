package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.interim.InterimCancelReminderEvent;
import com.modoo.application.common.dto.interim.InterimDeleteTodoEvent;
import com.modoo.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.modoo.application.common.port.reminder.out.ReminderLoaderPort;
import com.modoo.application.common.port.todo.in.DeleteTodoUseCase;
import com.modoo.application.common.port.todo.out.DeleteTodoPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.domain.planning.todo.aggregate.Todo;
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

  private final TodoLoaderPort todoLoaderPort;
  private final ReminderLoaderPort reminderLoaderPort;
  private final DeleteTodoPort deleteTodoPort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public void delete(Long loginId, Long todoId) {
    Optional<Todo> optionalTodo = todoLoaderPort.getOptionalTodo(todoId);

    if (optionalTodo.isEmpty()) {
      return;
    }

    Todo todo = optionalTodo.get();

    todo.validateTodoCreator(loginId);

    reminderLoaderPort.getRemindersByTodoId(todoId)
        .forEach(reminder ->
            applicationEventPublisher.publishEvent(
                InterimCancelReminderEvent.from(loginId, reminder)
            )
        );

    deleteTodoPort.delete(todo);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void publishNotificationEventAfterCommit(InterimDeleteTodoEvent event) {
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.from(event);

    applicationEventPublisher.publishEvent(removeEvent);
  }

}
