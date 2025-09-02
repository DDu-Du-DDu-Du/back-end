package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

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
class CancelReminderServiceTest {

  @Autowired
  CancelReminderService cancelReminderService;

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
    ddudu = DduduFixture.createRandomDduduWithGoalAndTime(
        goal,
        beginAt,
        null
    );
    ddudu = ddudu.moveDate(LocalDate.now()
        .plusDays(1));
    ddudu = saveDduduPort.save(ddudu.setReminder(0, 0, 10));
  }

  @Test
  void 미리알림_취소를_성공한다() {
    // given

    // when
    cancelReminderService.cancel(user.getId(), ddudu.getId());

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(ddudu.getId(), "not found");

    assertThat(actual.getRemindAt()).isNull();
  }

  @Test
  void 존재하지_않는_사용자는_미리알림_취소에_실패한다() {
    // given
    long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable cancel = () -> cancelReminderService.cancel(invalidId, ddudu.getId());

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(cancel)
        .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 존재하지_않는_뚜두는_미리알림_취소에_실패한다() {
    // given
    long invalidId = DduduFixture.getRandomId();

    // when
    ThrowingCallable cancel = () -> cancelReminderService.cancel(user.getId(), invalidId);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(cancel)
        .withMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 다른_사용자는_미리알림_취소에_실패한다() {
    // given
    User another = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable cancel = () -> cancelReminderService.cancel(another.getId(), ddudu.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(cancel)
        .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
