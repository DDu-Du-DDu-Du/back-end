package com.ddudu.domain.planning.repeatddudu.aggregate.vo;

import com.ddudu.common.exception.RepeatDduduErrorCode;
import com.ddudu.fixture.RepeatDduduFixture;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RepeatPatternTest {

  @Nested
  class 반복_뚜두_패턴_생성_테스트 {

    @ParameterizedTest
    @NullAndEmptySource
    void 위클리_반복_뚜두의_경우_반복_요일이_없으면_생성을_실패한다(List<String> repeatDaysOfWeek) {
      // given

      // when
      ThrowingCallable create = () -> new WeeklyRepeatPattern(repeatDaysOfWeek);

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_OR_EMPTY_REPEAT_DAYS_OF_WEEK.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 먼슬리_반복_뚜두의_경우_반복_날짜가_없으면_생성을_실패한다(List<Integer> repeatDaysOfMonth) {
      // given

      // when
      ThrowingCallable create = () -> new MonthlyRepeatPattern(repeatDaysOfMonth, null);

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(RepeatDduduErrorCode.NULL_OR_EMPTY_REPEAT_DATES_OF_MONTH.getCodeName());
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
          .plusMonths(1)
          .minusDays(1);
    }

    @Test
    void 데일리_반복_뚜두의_반복_날짜_리스트_조회에_성공한다() {
      // given
      RepeatPattern dailyPattern = RepeatDduduFixture.createDailyRepeatPattern();

      // when
      List<LocalDate> repeatDates = dailyPattern.calculateRepeatDates(startDate, endDate);

      // then
      Assertions.assertThat(repeatDates)
          .hasSize(startDate.until(endDate)
              .getDays() + 1);
    }

    @Test
    void 위클리_반복_뚜두의_반복_날짜_리스트_조회에_성공한다() {
      // given
      List<String> repeatDaysOfWeek = RepeatDduduFixture.getRandomRepeatDaysOfWeek(1);
      RepeatPattern weeklyPattern = RepeatDduduFixture.createWeeklyRepeatPattern(repeatDaysOfWeek);

      // when
      List<LocalDate> repeatDates = weeklyPattern.calculateRepeatDates(startDate, endDate);

      // then
      repeatDates.stream()
          .map(LocalDate::getDayOfWeek)
          .forEach(dayOfWeek -> Assertions.assertThat(repeatDaysOfWeek)
              .contains(dayOfWeek.toString()));
    }

    @Test
    void 먼슬리_반복_뚜두의_반복_날짜_리스트_조회에_성공한() {
      // given
      RepeatPattern monthlyPattern = RepeatDduduFixture.createMonthlyRepeatPattern(List.of(), true);

      // when
      List<LocalDate> repeatDates = monthlyPattern.calculateRepeatDates(startDate, endDate);

      // then
      Assertions.assertThat(repeatDates)
          .hasSize(1);
      repeatDates.stream()
          .map(LocalDate::getDayOfMonth)
          .forEach(dayOfMonth -> Assertions.assertThat(dayOfMonth)
              .isIn(startDate.lengthOfMonth()));
    }

  }

}