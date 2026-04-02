package com.ddudu.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(classes = ReminderListener.class)
@RecordApplicationEvents
class ReminderListenerTest {

  @Autowired
  ReminderListener reminderListener;

  @Test
  void 미리알림_설정_임시이벤트를_저장_이벤트로_변환해_발행한다(ApplicationEvents events) {
    //given
    Reminder reminder = Reminder.builder()
        .id(100L)
        .userId(1L)
        .todoId(2L)
        .remindsAt(LocalDateTime.now().plusHours(1))
        .build();
    InterimSetReminderEvent event = InterimSetReminderEvent.from(1L, reminder);

    //when
    reminderListener.publishSaveNotificationEvent(event);

    //then
    assertThat(events.stream(NotificationEventSaveEvent.class)).hasSize(1);
  }

  @Test
  void 미리알림_취소_임시이벤트를_삭제_이벤트로_변환해_발행한다(ApplicationEvents events) {
    //given
    Reminder reminder = Reminder.builder()
        .id(100L)
        .userId(1L)
        .todoId(2L)
        .remindsAt(LocalDateTime.now().plusHours(1))
        .build();
    InterimCancelReminderEvent event = InterimCancelReminderEvent.from(1L, reminder);

    //when
    reminderListener.publishRemoveNotificationEvent(event);

    //then
    assertThat(events.stream(NotificationEventRemoveEvent.class)).hasSize(1);
  }

}
