package com.ddudu.infra.mysql.planning.reminder.adapter;

import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.infra.mysql.planning.reminder.entity.ReminderEntity;
import com.ddudu.infra.mysql.planning.reminder.repository.ReminderRepository;
import java.util.List;
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
