package com.ddudu.domain.notification.event.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.common.exception.NotificationEventErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent.NotificationEventBuilder;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.fixture.NotificationEventFixture;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationEventTest {

  Long userId;
  Long contextId;
  NotificationEventTypeCode typeCode;

  Random random = new Random();

  @BeforeEach
  void setUp() {
    userId = NotificationEventFixture.getRandomId();
    contextId = NotificationEventFixture.getRandomId();
    int index = random.nextInt(NotificationEventTypeCode.values().length);
    typeCode = NotificationEventTypeCode.values()[index];
  }

  @Nested
  class 생성_테스트 {

    @Test
    void 알림_이벤트_생성을_성공한다() {
      // given
      NotificationEventBuilder builder = NotificationEvent.builder()
          .typeCode(typeCode)
          .senderId(userId)
          .receiverId(userId)
          .contextId(contextId);

      // when
      NotificationEvent event = builder.build();

      // then
      assertThat(event.getTypeCode()).isEqualTo(typeCode);
      assertThat(event.getReceiverId()).isEqualTo(userId);
      assertThat(event.getContextId()).isEqualTo(contextId);
    }

    @Test
    void 타입코드가_null이면_생성을_실패한다() {
      // given
      NotificationEventBuilder builder = NotificationEvent.builder()
          .receiverId(userId)
          .contextId(contextId);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(NotificationEventErrorCode.NULL_TYPE_CODE.getCodeName());
    }

    @Test
    void 수신자_ID가_null이면_생성을_실패한다() {
      // given
      NotificationEventBuilder builder = NotificationEvent.builder()
          .typeCode(typeCode)
          .contextId(contextId);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(NotificationEventErrorCode.NULL_RECEIVER_ID.getCodeName());
    }

    @Test
    void 컨텍스트_ID가_null이면_생성을_실패한다() {
      // given
      NotificationEventBuilder builder = NotificationEvent.builder()
          .typeCode(typeCode)
          .receiverId(userId);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(NotificationEventErrorCode.NULL_CONTEXT_ID.getCodeName());
    }

  }

  @Nested
  class 금일_발송_예정_테스트 {

    @Test
    void 오늘_발송_예정인_이벤트면_true를_반환한다() {
      // given
      NotificationEvent event = NotificationEventFixture.createValidEventNowWithUserAndContext(
          userId,
          typeCode,
          contextId
      );

      // when
      boolean actual = event.isPlannedToday();

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 오늘_발송_예정이_아니면_false를_반환한다() {
      // given
      LocalDateTime tomorrow = LocalDateTime.now()
          .plusDays(1);
      NotificationEvent event = NotificationEventFixture.createValidEventWithUserAndContext(
          userId,
          typeCode,
          contextId,
          tomorrow
      );

      // when
      boolean actual = event.isPlannedToday();

      // then
      assertThat(actual).isFalse();
    }

  }

  @Nested
  class 발송_여부_테스트 {

    @Test
    void 이미_발송된_이벤트는_true를_반환한다() {
      // given
      NotificationEvent fired = NotificationEventFixture.createFiredEventNowWithUserAndContext(
          userId,
          typeCode,
          contextId
      );

      // when
      boolean actual = fired.isAlreadyFired();

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 발송되지_않은_이벤트는_false를_반환한다() {
      // given
      NotificationEvent notFired = NotificationEventFixture.createValidEventNowWithUserAndContext(
          userId,
          typeCode,
          contextId
      );

      // when
      boolean actual = notFired.isAlreadyFired();

      // then
      assertThat(actual).isFalse();
    }

  }

  @Nested
  class 발송_예정_시간_수정_테스트 {

    @Test
    void 발송_예정_시간_수정을_성공한다() {
      // given
      LocalDateTime tomorrow = LocalDateTime.now()
          .plusDays(1);
      NotificationEvent notificationEvent = NotificationEventFixture.createValidEventWithUserAndContext(
          userId,
          typeCode,
          contextId,
          tomorrow
      );
      LocalDateTime expected = NotificationEventFixture.getFutureDateTime(1, TimeUnit.DAYS);

      // when
      NotificationEvent actual = notificationEvent.updateFireTime(expected);

      // then
      assertThat(actual.getWillFireAt()).isEqualTo(expected);
    }

    @Test
    void 이미_발송된_경우_시간_수정을_실패한다() {
      // given
      NotificationEvent notificationEvent = NotificationEventFixture.createFiredEventNowWithUserAndContext(
          userId,
          typeCode,
          contextId
      );
      LocalDateTime willFireAt = NotificationEventFixture.getFutureDateTime(1, TimeUnit.DAYS);

      // when
      ThrowingCallable update = () -> notificationEvent.updateFireTime(willFireAt);

      // then
      assertThatIllegalArgumentException().isThrownBy(update)
          .withMessage(NotificationEventErrorCode.CANNOT_MODIFY_FIRED_EVENT.getCodeName());
    }

    @Test
    void 발송_예정_시간이_현재시간보다_이전이면_시간_수정을_실패한다() {
      // given
      NotificationEvent notificationEvent = NotificationEventFixture.createValidEventNowWithUserAndContext(
          userId,
          typeCode,
          contextId
      );
      LocalDateTime willFireAt = NotificationEventFixture.getPastDateTime(1, TimeUnit.DAYS);

      // when
      ThrowingCallable update = () -> notificationEvent.updateFireTime(willFireAt);

      // then
      assertThatIllegalArgumentException().isThrownBy(update)
          .withMessage(NotificationEventErrorCode.CANNOT_FIRE_AT_PAST.getCodeName());
    }

  }

}
