package com.ddudu.domain.planning.periodgoal.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.fixture.PeriodGoalFixture;
import java.time.LocalDate;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PeriodGoalTest {

  @Nested
  class 기간_목표_생성_테스트 {

    Long userId;
    String contents;
    PeriodGoalType type;
    LocalDate planDate;

    @BeforeEach
    void setUp() {
      userId = PeriodGoalFixture.getRandomId();
      contents = PeriodGoalFixture.getRandomSentenceWithMax(255);
      type = PeriodGoalFixture.getRandomType();
      planDate = LocalDate.now();
    }

    @Test
    void 기간_목표_생성에_성공한다() {
      // when
      PeriodGoal periodGoal = PeriodGoal.builder()
          .userId(userId)
          .contents(contents)
          .type(type)
          .planDate(planDate)
          .build();

      // then
      assertThat(periodGoal)
          .extracting("userId", "contents", "type")
          .containsExactly(userId, contents, type);
    }

    @Test
    void 사용자_없이는_기간_목표를_생성할_수_없다() {
      // when
      ThrowingCallable create = () -> PeriodGoal.builder()
          .contents(contents)
          .userId(null)
          .type(type)
          .planDate(planDate)
          .build();

      // then
      Assertions.assertThatThrownBy(create)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());
    }

    @Test
    void 내용_없이는_기간_목표를_생성할_수_없다() {
      // when
      ThrowingCallable create = () -> PeriodGoal.builder()
          .contents(null)
          .userId(userId)
          .type(type)
          .planDate(planDate)
          .build();

      // then
      Assertions.assertThatThrownBy(create)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(PeriodGoalErrorCode.CONTENTS_NOT_EXISTING.getCodeName());
    }

  }

  @Nested
  class 기간_목표_작성자_검증_테스트 {

    PeriodGoal periodGoal;
    long userId;

    @BeforeEach
    void setUp() {
      userId = PeriodGoalFixture.getRandomId();
      periodGoal = PeriodGoalFixture.createRandomPeriodGoal(userId);
    }

    @Test
    void 사용자가_다르면_작성자_검증을_실패한다() {
      // given
      long invalidId = PeriodGoalFixture.getRandomId();

      while (Objects.equals(invalidId, userId)) {
        invalidId = PeriodGoalFixture.getRandomId();
      }

      long finalInvalidId = invalidId;

      // when
      ThrowingCallable validate = () -> periodGoal.validateCreator(finalInvalidId);

      // then
      assertThatExceptionOfType(SecurityException.class).isThrownBy(validate)
          .withMessage(PeriodGoalErrorCode.INVALID_AUTHORITY.getCodeName());
    }

  }

  @Nested
  class 기간_목표_수정_테스트 {

    long userId;
    PeriodGoal periodGoal;

    @BeforeEach
    void setUp() {
      userId = PeriodGoalFixture.getRandomId();
      periodGoal = PeriodGoalFixture.createRandomPeriodGoal(userId);
    }

    @Test
    void 기간_목표_수정에_성공한다() {
      // given
      String contents = PeriodGoalFixture.getRandomSentenceWithMax(255);

      // when
      PeriodGoal actual = periodGoal.update(contents);

      // then
      assertThat(actual.getContents()).isEqualTo(contents);
    }

  }

}
