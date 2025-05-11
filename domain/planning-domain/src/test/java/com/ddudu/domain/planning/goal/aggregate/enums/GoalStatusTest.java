package com.ddudu.domain.planning.goal.aggregate.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.fixture.GoalFixture;
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
class GoalStatusTest {

  @Nested
  class 목표_상태_생성_테스트 {

    @ParameterizedTest
    @ValueSource(strings = {"IN_PROGRESS", "in_progress", "DONE", "done"})
    void 목표_상태를_생성한다(String value) {
      // given
      String expected = value.toUpperCase();

      // when
      GoalStatus actual = GoalStatus.from(value);

      // then
      assertThat(actual.name()).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    void 기본_목표_상태는_진행중이다(String type) {
      // when
      GoalStatus actual = GoalStatus.from(type);

      // then
      assertThat(actual).isEqualTo(GoalStatus.IN_PROGRESS);
    }

    @Test
    void 잘못된_입력으로는_목표_상태_생성을_실패한다() {
      // given
      String value = GoalFixture.getRandomSentenceWithMax(10);

      // when
      ThrowingCallable create = () -> GoalStatus.from(value);

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(GoalErrorCode.INVALID_GOAL_STATUS.getCodeName());
    }

  }

}