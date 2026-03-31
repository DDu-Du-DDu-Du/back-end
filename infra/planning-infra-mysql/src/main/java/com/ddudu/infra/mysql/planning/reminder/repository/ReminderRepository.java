package com.ddudu.infra.mysql.planning.reminder.repository;

import com.ddudu.infra.mysql.planning.reminder.entity.ReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<ReminderEntity, Long> {

}
