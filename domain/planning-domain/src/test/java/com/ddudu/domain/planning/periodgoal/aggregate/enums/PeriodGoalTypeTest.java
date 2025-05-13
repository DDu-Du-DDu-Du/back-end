package com.ddudu.domain.planning.periodgoal.aggregate.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.fixture.PeriodGoalFixture;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PeriodGoalTypeTest {

  @Nested
  class 기간_목표_타입_생성_테스트 {

    @ParameterizedTest
    @ValueSource(strings = {"MONTH", "month", "WEEK", "week"})
    void 기간_목표를_생성한다(String value) {
      // given
      String expected = value.toUpperCase();

      // when
      PeriodGoalType actual = PeriodGoalType.from(value);

      // then
      assertThat(actual.name()).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    void 기간_목표_타입_없이는_기간_목표를_생성할_수_없다(String type) {
      // when
      ThrowingCallable create = () -> PeriodGoalType.from(type);

      // then
      assertThatThrownBy(create)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(PeriodGoalErrorCode.PERIOD_GOAL_TYPE_NOT_EXISTING.getCodeName());
    }

    @Test
    void 잘못된_입력으로는_기간_목표_타입_생성을_실패한다() {
      // given
      String value = PeriodGoalFixture.getRandomSentenceWithMax(10);

      // when
      ThrowingCallable create = () -> PeriodGoalType.from(value);

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(PeriodGoalErrorCode.INVALID_PERIOD_GOAL_TYPE_STATUS.getCodeName());
    }

  }

}