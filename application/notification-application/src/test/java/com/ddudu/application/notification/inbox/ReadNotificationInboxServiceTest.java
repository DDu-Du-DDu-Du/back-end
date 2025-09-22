package com.ddudu.application.notification.inbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.response.ReadNotificationInboxResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationInboxCommandPort;
import com.ddudu.common.exception.NotificationInboxErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.NotificationInbox;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.NotificationEventFixture;
import com.ddudu.fixture.NotificationInboxFixture;
import com.ddudu.fixture.UserFixture;
import java.util.MissingResourceException;
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
class ReadNotificationInboxServiceTest {

  @Autowired
  ReadNotificationInboxService readNotificationInboxService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  @Autowired
  NotificationInboxCommandPort notificationInboxCommandPort;

  User user;
  User anotherUser;
  NotificationInbox notificationInbox;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    Long dduduId = BaseFixture.getRandomId();
    NotificationEvent event = notificationEventCommandPort.save(
        NotificationEventFixture.createFiredDduduEventNowWithUserAndContext(user.getId(), dduduId)
    );

    notificationInbox = notificationInboxCommandPort.save(
        NotificationInboxFixture.createNotReadInboxSelfWithContextAndContent(
            event.getId(),
            user.getId(),
            event.getTypeCode(),
            event.getContextId(),
            BaseFixture.getRandomSentenceWithMax(50),
            BaseFixture.getRandomSentenceWithMax(200)
        )
    );
  }

  @Test
  void 알림_인박스를_읽음_처리한다() {
    // given

    // when
    ReadNotificationInboxResponse response = readNotificationInboxService.read(
        user.getId(),
        notificationInbox.getId()
    );

    // then
    String expected = notificationInbox.getTypeCode()
        .getUpstreamContext();

    assertThat(response.context()).isEqualTo(expected);
    assertThat(response.contextId()).isEqualTo(notificationInbox.getContextId());
  }

  @Test
  void 권한이_없는_사용자가_읽으면_실패한다() {
    // given

    // when
    ThrowingCallable read = () -> readNotificationInboxService.read(
        anotherUser.getId(),
        notificationInbox.getId()
    );

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(read)
        .withMessage(NotificationInboxErrorCode.NOT_AUTHORIZED_TO_INBOX.getCodeName());
  }

  @Test
  void 존재하지_않는_인박스_아이디면_실패한다() {
    // given
    long invalidId = BaseFixture.getRandomId();

    // when
    ThrowingCallable read = () -> readNotificationInboxService.read(
        user.getId(),
        invalidId
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(read)
        .withMessage(NotificationInboxErrorCode.INBOX_NOT_EXISTING.getCodeName());
  }

  @Test
  void 존재하지_않는_사용자면_실패한다() {
    // given
    long invalidId = BaseFixture.getRandomId();

    // when
    ThrowingCallable read = () -> readNotificationInboxService.read(
        invalidId,
        notificationInbox.getId()
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(read)
        .withMessage(NotificationInboxErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}

