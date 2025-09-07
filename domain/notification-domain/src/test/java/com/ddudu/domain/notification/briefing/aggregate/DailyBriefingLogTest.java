package com.ddudu.domain.notification.briefing.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.common.exception.DailyBriefingLogErrorCode;
import com.ddudu.domain.notification.briefing.aggregate.DailyBriefingLog.DailyBriefingLogBuilder;
import com.ddudu.fixture.DailyBriefingLogFixture;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DailyBriefingLogTest {

  Long userId;
  LocalDate briefingDate;

  @BeforeEach
  void setUp() {
    userId = DailyBriefingLogFixture.getRandomId();
    briefingDate = DailyBriefingLogFixture.getFutureDateTime(1, TimeUnit.MINUTES)
        .toLocalDate();
  }

  @Nested
  class 생성_테스트 {

    @Test
    void 데일리_사전_요약을_생성한다() {
      // given
      DailyBriefingLogBuilder builder = DailyBriefingLog.builder()
          .userId(userId)
          .briefingDate(briefingDate);

      // when
      DailyBriefingLog actual = builder.build();

      // then
      assertThat(actual.getUserId()).isEqualTo(userId);
      assertThat(actual.getBriefingDate()).isEqualTo(briefingDate);
    }

    @Test
    void 사전_요약_날짜가_없으면_오늘의_데일리_요약을_생성한다() {
      // given
      DailyBriefingLogBuilder builder = DailyBriefingLog.builder()
          .userId(userId);

      // when
      DailyBriefingLog actual = builder.build();

      // then
      assertThat(actual.getBriefingDate()).isToday();
    }

    @Test
    void 유저가_없으면_데일리_요약_생성을_실패한다() {
      // given
      DailyBriefingLogBuilder builder = DailyBriefingLog.builder()
          .briefingDate(briefingDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(DailyBriefingLogErrorCode.NULL_USER_ID.getCodeName());
    }

  }

}