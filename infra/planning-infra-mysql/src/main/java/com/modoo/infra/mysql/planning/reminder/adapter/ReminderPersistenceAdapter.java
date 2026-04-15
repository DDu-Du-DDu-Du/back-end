package com.modoo.infra.mysql.planning.reminder.adapter;

import com.modoo.application.common.port.reminder.out.ReminderCommandPort;
import com.modoo.application.common.port.reminder.out.ReminderLoaderPort;
import com.modoo.common.annotation.DrivenAdapter;
import com.modoo.common.exception.ReminderErrorCode;
import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.infra.mysql.planning.reminder.entity.ReminderEntity;
import com.modoo.infra.mysql.planning.reminder.repository.ReminderRepository;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class ReminderPersistenceAdapter implements ReminderCommandPort, ReminderLoaderPort {

  private final ReminderRepository reminderRepository;

  @Override
  public Reminder save(Reminder reminder) {
    return reminderRepository.save(ReminderEntity.from(reminder))
        .toDomain();
  }

  @Override
  public Reminder update(Reminder reminder) {
    ReminderEntity reminderEntity = reminderRepository.findById(reminder.getId())
        .orElseThrow(() ->
            new MissingResourceException(
                ReminderErrorCode.REMINDER_NOT_EXISTING.getCodeName(),
                ReminderEntity.class.getName(),
                String.valueOf(reminder.getId())
            )
        );
    reminderEntity.update(reminder);

    return reminderEntity.toDomain();
  }

  @Override
  public void deleteById(Long id) {
    reminderRepository.deleteById(id);
  }

  @Override
  public Optional<Reminder> getOptionalReminder(Long id) {
    return reminderRepository.findById(id)
        .map(ReminderEntity::toDomain);
  }

  @Override
  public List<Reminder> getRemindersByTodoId(Long todoId) {
    return reminderRepository.findAllByTodoIdOrderByRemindsAtAsc(todoId)
        .stream()
        .map(ReminderEntity::toDomain)
        .toList();
  }

}
