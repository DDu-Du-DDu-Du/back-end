package com.ddudu.domain.planning.periodgoal.aggregate.enums;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.domain.planning.periodgoal.exception.PeriodGoalErrorCode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PeriodGoalTypeTest {

  @Nested
  class 기간_목표_타입_생성_테스트 {

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

  }

}