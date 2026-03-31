package com.ddudu.infra.mysql.planning.reminder.repository;

import com.ddudu.infra.mysql.planning.reminder.entity.ReminderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<ReminderEntity, Long> {

  List<ReminderEntity> findAllByTodoIdOrderByRemindsAtAsc(Long todoId);

}
