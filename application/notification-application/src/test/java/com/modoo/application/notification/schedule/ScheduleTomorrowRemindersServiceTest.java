package com.modoo.application.notification.schedule;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.notification.event.NotificationScheduleEvent;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.notification.in.ScheduleTomorrowRemindersUseCase;
import com.modoo.application.common.port.notification.out.NotificationEventCommandPort;
import com.modoo.domain.notification.event.aggregate.NotificationEvent;
import com.modoo.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.NotificationEventFixture;
import com.modoo.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
@RecordApplicationEvents
class ScheduleTomorrowRemindersServiceTest {

  @Autowired
  ScheduleTomorrowRemindersUseCase scheduleTomorrowRemindersUseCase;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  @Autowired
  SignUpPort signUpPort;

  User user;
  LocalDateTime tomorrowMorning;
  LocalDateTime tomorrowAfternoon;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    LocalDate tomorrow = LocalDate.now()
        .plusDays(1);
    tomorrowMorning = tomorrow.atTime(10, 0);
    tomorrowAfternoon = tomorrow.atTime(15, 0);
  }

  @Test
  void 내일_발송_대상_모두_스케줄링한다(ApplicationEvents events) {
    // given
    NotificationEvent tomorrowMorningReminder = notificationEventCommandPort.save(
        NotificationEventFixture.createValidEventWithUserAndContext(
            user.getId(),
            NotificationEventTypeCode.TODO_REMINDER,
            UserFixture.getRandomId(),
            tomorrowMorning
        )
    );
    NotificationEvent tomorrowAfternoonReminder = notificationEventCommandPort.save(
        NotificationEventFixture.createValidEventWithUserAndContext(
            user.getId(),
            NotificationEventTypeCode.TODO_REMINDER,
            UserFixture.getRandomId(),
            tomorrowAfternoon
        )
    );

    // when
    scheduleTomorrowRemindersUseCase.registerAllTomorrowReminders();

    // then
    assertThat(events.stream(NotificationScheduleEvent.class)
        .map(NotificationScheduleEvent::eventId))
        .contains(tomorrowMorningReminder.getId(), tomorrowAfternoonReminder.getId());
    assertThat(events.stream(NotificationScheduleEvent.class)
        .map(NotificationScheduleEvent::willFireAt))
        .contains(tomorrowMorning, tomorrowAfternoon);
  }

  @Test
  void 여러_사용자의_대상_모두_스케줄링한다(ApplicationEvents events) {
    // given
    User other = signUpPort.save(UserFixture.createRandomUserWithId());
    NotificationEvent userReminder = notificationEventCommandPort.save(
        NotificationEventFixture.createValidEventWithUserAndContext(
            user.getId(),
            NotificationEventTypeCode.TODO_REMINDER,
            UserFixture.getRandomId(),
            tomorrowMorning
        )
    );
    NotificationEvent otherUserReminder = notificationEventCommandPort.save(
        NotificationEventFixture.createValidEventWithUserAndContext(
            other.getId(),
            NotificationEventTypeCode.TODO_REMINDER,
            UserFixture.getRandomId(),
            tomorrowAfternoon
        )
    );

    // when
    scheduleTomorrowRemindersUseCase.registerAllTomorrowReminders();

    // then
    assertThat(events.stream(NotificationScheduleEvent.class)
        .map(NotificationScheduleEvent::eventId))
        .contains(userReminder.getId(), otherUserReminder.getId());
  }

  @Test
  void 내일이_아닌_대상은_스케줄링되지_않는다(ApplicationEvents events) {
    // given
    LocalDateTime today = LocalDate.now()
        .atTime(9, 0);
    LocalDateTime dayAfterTomorrow = LocalDate.now()
        .plusDays(2)
        .atTime(9, 0);

    notificationEventCommandPort.save(
        NotificationEventFixture.createValidEventWithUserAndContext(
            user.getId(),
            NotificationEventTypeCode.TODO_REMINDER,
            UserFixture.getRandomId(),
            today
        )
    );
    notificationEventCommandPort.save(
        NotificationEventFixture.createValidEventWithUserAndContext(
            user.getId(),
            NotificationEventTypeCode.TODO_REMINDER,
            UserFixture.getRandomId(),
            dayAfterTomorrow
        )
    );

    // when
    scheduleTomorrowRemindersUseCase.registerAllTomorrowReminders();

    // then
    assertThat(events.stream(NotificationScheduleEvent.class)).isEmpty();
  }

}
