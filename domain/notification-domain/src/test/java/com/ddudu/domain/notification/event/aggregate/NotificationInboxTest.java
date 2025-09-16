package com.ddudu.domain.notification.event.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.common.exception.NotificationInboxErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationInbox.NotificationInboxBuilder;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.fixture.NotificationInboxFixture;
import java.util.Random;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationInboxTest {

  Long userId;
  Long senderId;
  Long eventId;
  Long contextId;
  NotificationEventTypeCode typeCode;
  String title;
  String body;
  Random random = new Random();

  @BeforeEach
  void setUp() {
    userId = NotificationInboxFixture.getRandomId();
    senderId = NotificationInboxFixture.getRandomId();
    eventId = NotificationInboxFixture.getRandomId();
    contextId = NotificationInboxFixture.getRandomId();
    int index = random.nextInt(NotificationEventTypeCode.values().length);
    typeCode = NotificationEventTypeCode.values()[index];
    title = NotificationInboxFixture.getRandomSentenceWithMax(50);
    body = NotificationInboxFixture.getRandomSentenceWithMax(200);
  }

  @Nested
  class 생성_테스트 {

    @Test
    void 알림_인박스_생성을_성공한다() {
      // given
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .userId(userId)
          .senderId(senderId)
          .eventId(eventId)
          .contextId(contextId)
          .typeCode(typeCode)
          .title(title)
          .body(body);

      // when
      NotificationInbox actual = builder.build();

      // then
      assertThat(actual.getUserId()).isEqualTo(userId);
      assertThat(actual.getSenderId()).isEqualTo(senderId);
      assertThat(actual.getEventId()).isEqualTo(eventId);
      assertThat(actual.getContextId()).isEqualTo(contextId);
      assertThat(actual.getTypeCode()).isEqualTo(typeCode);
      assertThat(actual.getTitle()).isEqualTo(title);
      assertThat(actual.getBody()).isEqualTo(body);
    }

    @Test
    void 대상_사용자가_없으면_알림_인박스_생성을_실패한다() {
      // given
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .senderId(senderId)
          .eventId(eventId)
          .contextId(contextId)
          .typeCode(typeCode)
          .title(title)
          .body(body);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(NotificationInboxErrorCode.NULL_USER_ID.getCodeName());
    }

    @Test
    void 대상_알림_이벤트가_없으면_알림_인박스_생성을_실패한다() {
      // given
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .senderId(senderId)
          .userId(userId)
          .contextId(contextId)
          .typeCode(typeCode)
          .title(title)
          .body(body);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(NotificationInboxErrorCode.NULL_EVENT_ID.getCodeName());
    }

    @Test
    void 대상_알림_이벤트_유형이_없으면_알림_인박스_생성을_실패한다() {
      // given
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .senderId(senderId)
          .eventId(eventId)
          .contextId(contextId)
          .userId(userId)
          .title(title)
          .body(body);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(NotificationInboxErrorCode.NULL_TYPE_CODE.getCodeName());
    }

    @Test
    void 대상_컨텍스트가_없으면_알림_인박스_생성을_실패한다() {
      // given
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .senderId(senderId)
          .eventId(eventId)
          .userId(userId)
          .typeCode(typeCode)
          .title(title)
          .body(body);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(NotificationInboxErrorCode.NULL_CONTEXT_ID.getCodeName());
    }

    @Test
    void 알림_제목이_없으면_알림_인박스_생성을_실패한다() {
      // given
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .senderId(senderId)
          .eventId(eventId)
          .contextId(contextId)
          .typeCode(typeCode)
          .userId(userId)
          .body(body);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(NotificationInboxErrorCode.NULL_TITLE.getCodeName());
    }

    @Test
    void 알림_제목이_50자가_넘으면_알림_인박스_생성을_실패한다() {
      // given
      String longTitle = NotificationInboxFixture.getRandomSentence(51, 100);
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .senderId(senderId)
          .userId(userId)
          .eventId(eventId)
          .contextId(contextId)
          .typeCode(typeCode)
          .title(longTitle)
          .body(body);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(NotificationInboxErrorCode.EXCESSIVE_TITLE_LENGTH.getCodeName());
    }

    @Test
    void 알림_내용이_200자가_넘으면_알림_인박스_생성을_실패한다() {
      // given
      String longBody = NotificationInboxFixture.getRandomSentence(201, 300);
      NotificationInboxBuilder builder = NotificationInbox.builder()
          .senderId(senderId)
          .userId(userId)
          .eventId(eventId)
          .contextId(contextId)
          .typeCode(typeCode)
          .title(title)
          .body(longBody);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(NotificationInboxErrorCode.EXCESSIVE_BODY_LENGTH.getCodeName());
    }

  }

}