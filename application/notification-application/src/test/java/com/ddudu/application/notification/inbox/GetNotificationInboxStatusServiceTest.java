package com.ddudu.application.notification.inbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.response.NotificationInboxStatusResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationInboxCommandPort;
import com.ddudu.common.exception.NotificationInboxErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GetNotificationInboxStatusServiceTest {

  @Autowired
  GetNotificationInboxStatusService getNotificationInboxStatusService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  NotificationInboxCommandPort notificationInboxCommandPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  User user;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
  }

  @Nested
  class 상태_조회_성공_케이스 {

    int unreadCount;

    @BeforeEach
    void setUpInboxes() {
      // given
      unreadCount = BaseFixture.getRandomInt(2, 5);
      int readCount = BaseFixture.getRandomInt(1, 3);

      for (int i = 0; i < unreadCount; i++) {
        NotificationEvent event = notificationEventCommandPort.save(
            NotificationEventFixture.createFiredTodoEventNowWithUserAndContext(
                user.getId(),
                BaseFixture.getRandomId()
            )
        );
        notificationInboxCommandPort.save(
            NotificationInboxFixture.createUnreadInboxWithRandomContentFromNotificationEvent(event)
        );
      }
      for (int i = 0; i < readCount; i++) {
        NotificationEvent event = notificationEventCommandPort.save(
            NotificationEventFixture.createFiredTodoEventNowWithUserAndContext(
                user.getId(),
                BaseFixture.getRandomId()
            )
        );
        notificationInboxCommandPort.save(
            NotificationInboxFixture.createReadInboxWithRandomContentFromNotificationEvent(event)
        );
      }
    }

    @Test
    void 읽지_않은_알림이_있으면_상태_조회에_성공한다() {
      // given

      // when
      NotificationInboxStatusResponse response = getNotificationInboxStatusService.getStatus(user.getId());

      // then
      assertThat(response.hasNew()).isTrue();
      assertThat(response.unreadCount()).isEqualTo(unreadCount);
    }
  }

  @Nested
  class 상태_조회_새_알림_없음_케이스 {

    @BeforeEach
    void setUpInboxes() {
      // given
      int readCount = BaseFixture.getRandomInt(1, 3);

      for (int i = 0; i < readCount; i++) {
        NotificationEvent event = notificationEventCommandPort.save(
            NotificationEventFixture.createFiredTodoEventNowWithUserAndContext(
                user.getId(),
                BaseFixture.getRandomId()
            )
        );
        notificationInboxCommandPort.save(
            NotificationInboxFixture.createReadInboxWithRandomContentFromNotificationEvent(event)
        );
      }
    }

    @Test
    void 읽지_않은_알림이_없으면_hasNew는_false다() {
      // given

      // when
      NotificationInboxStatusResponse response = getNotificationInboxStatusService.getStatus(user.getId());

      // then
      assertThat(response.hasNew()).isFalse();
      assertThat(response.unreadCount()).isZero();
    }
  }

  @Test
  void 존재하지_않는_로그인_사용자면_실패한다() {
    // given
    long invalidLoginId = BaseFixture.getRandomId();

    // when
    ThrowingCallable getStatus = () -> getNotificationInboxStatusService.getStatus(invalidLoginId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(getStatus)
        .withMessage(NotificationInboxErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}
