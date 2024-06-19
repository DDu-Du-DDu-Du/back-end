package com.ddudu.application.domain.repeat_ddudu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu.RepeatDduduBuilder;
import com.ddudu.application.domain.repeat_ddudu.domain.enums.RepeatType;
import com.ddudu.application.domain.repeat_ddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.dto.repeat_ddudu.RepeatPatternDto;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatDduduFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RepeatDduduTest {

  Long goalId;

  @BeforeEach
  void setUp() {
    goalId = GoalFixture.getRandomId();
  }

  @Nested
  class 생성_테스트 {

    String name;
    LocalDate startDate;
    LocalDate endDate;
    RepeatType repeatType;
    RepeatPattern repeatPattern;

    @BeforeEach
    void setUp() {
      name = RepeatDduduFixture.getRandomSentenceWithMax(50);
      startDate = LocalDate.now();
      endDate = LocalDate.now()
          .plusMonths(1);
      repeatType = RepeatDduduFixture.getRandomRepeatType();
      repeatPattern = RepeatDduduFixture.createRandomRepeatPattern(repeatType);
    }

    @Test
    void 반복_뚜두_생성을_성공한다() {
      // when
      RepeatDdudu repeatDdudu = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .startDate(startDate)
          .endDate(endDate)
          .build();

      // then
      assertThat(repeatDdudu).isNotNull();
      assertThat(repeatDdudu)
          .hasFieldOrPropertyWithValue("goalId", goalId)
          .hasFieldOrPropertyWithValue("name", name)
          .hasFieldOrPropertyWithValue("repeatType", repeatType)
          .hasFieldOrPropertyWithValue("startDate", startDate)
          .hasFieldOrPropertyWithValue("endDate", endDate);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 이름이_빈_값이면_생성을_실패한다(String blankName) {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(blankName)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .startDate(startDate)
          .endDate(endDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.BLANK_NAME.getCodeName());
    }

    @Test
    void 이름이_50자를_넘으면_생성을_실패한다() {
      // given
      String over50 = RepeatDduduFixture.getRandomSentence(51, 100);
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(over50)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .startDate(startDate)
          .endDate(endDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
    }

    @Test
    void 목표가_없으면_생성을_실패한다() {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .name(name)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .startDate(startDate)
          .endDate(endDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    }

    @Test
    void 반복_유형이_없으면_생성을_실패한다() {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatPattern(repeatPattern)
          .startDate(startDate)
          .endDate(endDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_REPEAT_TYPE.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 위클리_반복_뚜두의_경우_반복_요일이_없으면_생성을_실패한다(List<String> repeatDaysOfWeek) {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatType(RepeatType.WEEKLY)
          .repeatPatternDto(RepeatPatternDto.weeklyPatternOf(repeatDaysOfWeek))
          .startDate(startDate)
          .endDate(endDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_OR_EMPTY_REPEAT_DAYS_OF_WEEK.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 먼슬리_반복_뚜두의_경우_반복_날짜가_없으면_생성을_실패한다(List<Integer> repeatDaysOfMonth) {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatType(RepeatType.MONTHLY)
          .repeatPatternDto(RepeatPatternDto.monthlyPatternOf(repeatDaysOfMonth))
          .startDate(startDate)
          .endDate(endDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_OR_EMPTY_REPEAT_DATES_OF_MONTH.getCodeName());
    }

    @Test
    void 시작_날짜가_없으면_생성을_실패한다() {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .endDate(endDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_START_DATE.getCodeName());
    }

    @Test
    void 종료_날짜가_없으면_생성을_실패한다() {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .startDate(startDate);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_END_DATE.getCodeName());
    }

    @Test
    void 시작_날짜가_종료_날짜보다_뒤면_생성을_실패한다() {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .startDate(startDate)
          .endDate(startDate.minusMonths(1));

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.UNABLE_TO_END_BEFORE_START.getCodeName());
    }

    @Test
    void 시작_시간이_종료_시간보다_뒤면_생성을_실패한다() {
      // given
      RepeatDduduBuilder builder = RepeatDdudu.builder()
          .goalId(goalId)
          .name(name)
          .repeatType(repeatType)
          .repeatPattern(repeatPattern)
          .startDate(startDate)
          .endDate(endDate)
          .beginAt(LocalTime.now()
              .plusMinutes(1))
          .endAt(LocalTime.now());

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
    }

  }

  @Nested
  class 반복_날짜_테스트 {

    LocalDate startDate;
    LocalDate endDate;

    @BeforeEach
    void setUp() {
      startDate = LocalDate.now();
      endDate = LocalDate.now()
          .plusMonths(1);
    }

    @Test
    void 데일리_반복_뚜두의_반복_날짜_리스트_조회에_성공한다() {
      // given
      RepeatPattern dailyPattern = RepeatDduduFixture.createDailyRepeatPattern();
      RepeatDdudu repeatDdudu = RepeatDduduFixture.createRepeatDdudu(
          RepeatType.DAILY,
          dailyPattern,
          startDate,
          endDate
      );

      // when
      List<LocalDate> repeatDates = repeatDdudu.getRepeatDates();

      // then
      assertThat(repeatDates).hasSize(startDate.until(endDate)
          .getDays() + 1);
    }

    @Test
    void 위클리_반복_뚜두의_반복_날짜_리스트_조회에_성공한다() {
      // given
      List<String> repeatDaysOfWeek = RepeatDduduFixture.getRandomRepeatDaysOfWeek(1);
      RepeatPattern weeklyPattern = RepeatDduduFixture.createWeeklyRepeatPattern(
          repeatDaysOfWeek);
      RepeatDdudu repeatDdudu = RepeatDduduFixture.createRepeatDdudu(
          RepeatType.WEEKLY,
          weeklyPattern,
          startDate,
          endDate
      );

      // when
      List<LocalDate> repeatDates = repeatDdudu.getRepeatDates();

      // then
      repeatDates.stream()
          .map(LocalDate::getDayOfWeek)
          .forEach(dayOfWeek -> assertThat(repeatDaysOfWeek)
              .contains(dayOfWeek.toString()));
    }

    @Test
    void 먼슬리_반복_뚜두의_반복_날짜_리스트_조회에_성공한() {
      // given
      int repeatDay = RepeatDduduFixture.getRandomInt(1, 25);
      RepeatPattern monthlyPattern = RepeatDduduFixture.createMonthlyRepeatPattern(
          List.of(repeatDay), true);
      RepeatDdudu repeatDdudu = RepeatDduduFixture.createRepeatDdudu(
          RepeatType.MONTHLY,
          monthlyPattern,
          startDate,
          endDate
      );

      // when
      List<LocalDate> repeatDates = repeatDdudu.getRepeatDates();

      // then
      assertThat(repeatDates).hasSize(2);
      repeatDates.stream()
          .map(LocalDate::getDayOfMonth)
          .forEach(dayOfMonth ->
              assertThat(dayOfMonth).isIn(repeatDay, startDate.lengthOfMonth()));
    }

  }

}
