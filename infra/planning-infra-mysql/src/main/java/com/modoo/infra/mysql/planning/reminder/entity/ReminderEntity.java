package com.modoo.infra.mysql.planning.reminder.entity;

import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reminders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReminderEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(
      name = "todo_id",
      nullable = false
  )
  private Long todoId;

  @Column(
      name = "reminds_at",
      nullable = false,
      columnDefinition = "TIMESTAMP"
  )
  private LocalDateTime remindsAt;

  @Column(
      name = "reminded_at",
      columnDefinition = "TIMESTAMP"
  )
  private LocalDateTime remindedAt;

  public static ReminderEntity from(Reminder reminder) {
    return ReminderEntity.builder()
        .id(reminder.getId())
        .userId(reminder.getUserId())
        .todoId(reminder.getTodoId())
        .remindsAt(reminder.getRemindsAt())
        .remindedAt(reminder.getRemindedAt())
        .build();
  }

  public Reminder toDomain() {
    return Reminder.builder()
        .id(id)
        .userId(userId)
        .todoId(todoId)
        .remindsAt(remindsAt)
        .remindedAt(remindedAt)
        .build();
  }

  public void update(Reminder reminder) {
    this.remindsAt = reminder.getRemindsAt();
    this.remindedAt = reminder.getRemindedAt();
  }

}
