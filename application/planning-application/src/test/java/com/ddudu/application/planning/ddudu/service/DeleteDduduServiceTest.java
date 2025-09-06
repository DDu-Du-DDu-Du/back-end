package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.NotificationEventFixture;
import com.ddudu.fixture.UserFixture;
import java.util.Optional;
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
class DeleteDduduServiceTest {

  @Autowired
  DeleteDduduService deleteDduduService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  NotificationEventLoaderPort notificationEventLoaderPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  User user;
  Ddudu ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
  }

  @Test
  void 뚜두를_삭제_할_수_있다() {
    // when
    deleteDduduService.delete(user.getId(), ddudu.getId());

    // then
    assertThat(dduduLoaderPort.getOptionalDdudu(ddudu.getId())).isEmpty();
  }

  @Test
  void 뚜두가_존재하지_않는_경우_예외가_발생하지_않는다() {
    // given
    Long invalidId = DduduFixture.getRandomId();

    // when
    ThrowingCallable delete = () -> deleteDduduService.delete(user.getId(), invalidId);

    // then
    assertThatNoException().isThrownBy(delete);
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable delete = () -> deleteDduduService.delete(anotherUser.getId(), ddudu.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(delete)
        .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  @Test
  void 뚜두_삭제_시_연관된_알림도_삭제된다() {
    // given: register a notification event for this ddudu context
    NotificationEvent event = NotificationEventFixture.createValidDduduEventNowWithUserAndContext(
        user.getId(),
        ddudu.getId()
    );

    notificationEventCommandPort.save(event);

    // when
    deleteDduduService.delete(user.getId(), ddudu.getId());

    // then
    Optional<NotificationEvent> actual = notificationEventLoaderPort.getOptionalEventByContext(
        NotificationEventTypeCode.DDUDU,
        ddudu.getId()
    );

    assertThat(actual).isEmpty();
  }

}
