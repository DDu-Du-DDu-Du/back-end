package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.SetReminderRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
class SetReminderServiceTest {

  @Autowired
  SetReminderService setReminderService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  User user;
  Goal goal;
  Ddudu ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    LocalTime beginAt = LocalTime.MAX;
    Ddudu temp = DduduFixture.createRandomDduduWithGoalAndTime(
        goal,
        beginAt,
        null
    );
    ddudu = saveDduduPort.save(temp.moveDate(LocalDate.now()
        .plusDays(1)));
  }

  @Test
  void 미리알림을_설정한다() {
    // given
    SetReminderRequest request = new SetReminderRequest(0, 0, 30);

    // when
    setReminderService.setReminder(user.getId(), ddudu.getId(), request);

    // then
    Ddudu actualDdudu = dduduLoaderPort.getDduduOrElseThrow(ddudu.getId(), "not found");
    LocalDateTime expected = ddudu.getScheduledOn()
        .atTime(ddudu.getBeginAt())
        .minusHours(request.hours())
        .minusMinutes(request.minutes());

    assertThat(actualDdudu.getRemindAt()).isEqualTo(expected);
  }

  @Test
  void 존재하지_않는_사용자는_미리알림_설정에_실패한다() {
    // given
    long invalidId = DduduFixture.getRandomId();
    SetReminderRequest request = new SetReminderRequest(0, 1, 0);

    // when
    ThrowingCallable setReminder = () -> setReminderService.setReminder(
        invalidId,
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(setReminder)
        .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 존재하지_않는_뚜두는_미리알림_설정에_실패한다() {
    // given
    long invalidId = DduduFixture.getRandomId();
    SetReminderRequest request = new SetReminderRequest(0, 1, 0);

    // when
    ThrowingCallable setReminder = () -> setReminderService.setReminder(
        user.getId(),
        invalidId,
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(setReminder)
        .withMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 다른_사용자는_미리알림_설정에_실패한다() {
    // given
    User another = signUpPort.save(UserFixture.createRandomUserWithId());
    SetReminderRequest request = new SetReminderRequest(0, 1, 0);

    // when
    ThrowingCallable setReminder = () -> setReminderService.setReminder(
        another.getId(),
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(setReminder)
        .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
