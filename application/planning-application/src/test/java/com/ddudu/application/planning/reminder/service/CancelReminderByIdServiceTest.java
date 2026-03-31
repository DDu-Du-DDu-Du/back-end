package com.ddudu.application.planning.reminder.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.common.exception.UnprocessableEntityException;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.ReminderFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CancelReminderByIdServiceTest {

  @Autowired
  CancelReminderByIdService cancelReminderByIdService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  ReminderCommandPort reminderCommandPort;

  @Autowired
  ReminderLoaderPort reminderLoaderPort;

  User user;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
  }

  @Test
  void 미리알림_취소에_성공한다() {
    // given
    Reminder reminder = reminderCommandPort.save(ReminderFixture.createReminderWithUserId(user.getId()));

    // when
    cancelReminderByIdService.cancel(user.getId(), reminder.getId());

    // then
    assertThat(reminderLoaderPort.getOptionalReminder(reminder.getId())).isEmpty();
  }

  @Test
  void 존재하지_않는_미리알림_아이디면_성공한다() {
    // given
    Long unknownReminderId = TodoFixture.getRandomId();

    // when
    cancelReminderByIdService.cancel(user.getId(), unknownReminderId);

    // then

  }

  @Test
  void 로그인_사용자가_없으면_실패한다() {
    // given
    Long invalidLoginId = TodoFixture.getRandomId();
    Reminder reminder = reminderCommandPort.save(ReminderFixture.createReminderWithUserId(user.getId()));

    // when
    ThrowingCallable cancel = () -> cancelReminderByIdService.cancel(invalidLoginId, reminder.getId());

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(cancel)
        .withMessage(ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 이미_발송된_미리알림이면_실패한다() {
    // given
    Reminder remindedReminder = ReminderFixture.createReminderWithRemindedAt(LocalDateTime.now());
    Reminder reminder = reminderCommandPort.save(
        Reminder.builder()
            .userId(user.getId())
            .todoId(remindedReminder.getTodoId())
            .remindsAt(remindedReminder.getRemindsAt())
            .remindedAt(remindedReminder.getRemindedAt())
            .build()
    );

    // when
    ThrowingCallable cancel = () -> cancelReminderByIdService.cancel(user.getId(), reminder.getId());

    // then
    Assertions.assertThatExceptionOfType(UnprocessableEntityException.class)
        .isThrownBy(cancel)
        .withMessage(ReminderErrorCode.ALREADY_REMINDED.getCodeName());
  }

}
