package com.ddudu.domain.planning.repeatddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import com.ddudu.application.planning.repeatddudu.dto.request.CreateRepeatDduduRequest;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatDduduFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(RepeatDduduDomainService.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class RepeatDduduDomainServiceTest {

  @Autowired
  RepeatDduduDomainService repeatDduduDomainService;

  @Nested
  class 반복_뚜두_생성_테스트 {

    String name;
    Long goalId;
    LocalDate startDate;
    LocalDate endDate;

    @BeforeEach
    void setUp() {
      name = RepeatDduduFixture.getRandomSentenceWithMax(50);
      goalId = GoalFixture.getRandomId();
      startDate = LocalDate.now();
      endDate = LocalDate.now()
          .plusMonths(1);
    }

    @Test
    void 데일리_반복_뚜두를_생성한다() {
      // given
      CreateRepeatDduduRequest request = new CreateRepeatDduduRequest(
          name,
          goalId,
          RepeatType.DAILY.name(),
          null,
          null,
          null,
          startDate,
          endDate,
          null,
          null
      );

      // when
      RepeatDdudu actual = repeatDduduDomainService.create(request);

      // then
      assertThat(actual).extracting("name", "goalId", "repeatType", "startDate", "endDate")
          .containsExactly(name, goalId, RepeatType.DAILY, startDate, endDate);
    }

    @Test
    void 위클리_반복_뚜두를_생성한다() {
      // given
      CreateRepeatDduduRequest request = new CreateRepeatDduduRequest(
          name,
          goalId,
          RepeatType.WEEKLY.name(),
          RepeatDduduFixture.getRandomRepeatDaysOfWeek(),
          null,
          null,
          startDate,
          endDate,
          null,
          null
      );

      // when
      RepeatDdudu actual = repeatDduduDomainService.create(request);

      // then
      assertThat(actual).extracting("name", "goalId", "repeatType", "startDate", "endDate")
          .containsExactly(name, goalId, RepeatType.WEEKLY, startDate, endDate);
    }

    @Test
    void 먼슬리_반복_뚜두를_생성한다() {
      // given
      CreateRepeatDduduRequest request = new CreateRepeatDduduRequest(
          name,
          goalId,
          RepeatType.MONTHLY.name(),
          null,
          RepeatDduduFixture.getRandomRepeatDaysOfMonth(1),
          true,
          startDate,
          endDate,
          null,
          null
      );

      // when
      RepeatDdudu actual = repeatDduduDomainService.create(request);

      // then
      assertThat(actual).extracting("name", "goalId", "repeatType", "startDate", "endDate")
          .containsExactly(name, goalId, RepeatType.MONTHLY, startDate, endDate);
    }

  }

  @Nested
  class 뚜두_생성_테스트 {

    Long userId;
    String name;
    Long goalId;
    LocalDate startDate;
    LocalDate endDate;

    @BeforeEach
    void setUp() {
      userId = GoalFixture.getRandomId();
      name = RepeatDduduFixture.getRandomSentenceWithMax(50);
      goalId = GoalFixture.getRandomId();
      startDate = LocalDate.now();
      endDate = LocalDate.now()
          .plusMonths(1);
    }

    @Test
    void 데일리_반복_뚜두의_뚜두를_생성한다() {
      // given
      RepeatDdudu dailyRepeatDdudu = RepeatDduduFixture.createRepeatDdudu(
          RepeatType.DAILY,
          RepeatDduduFixture.createDailyRepeatPattern(),
          startDate,
          endDate
      );

      // when
      List<Ddudu> ddudus = repeatDduduDomainService.createRepeatedDdudus(
          userId, dailyRepeatDdudu);

      // then
      Assertions.assertThat(ddudus).hasSize(startDate.until(endDate)
          .getDays() + 1);
      ddudus.stream()
          .map(Ddudu::getScheduledOn)
          .forEach(date -> Assertions.assertThat(date).isBetween(startDate, endDate));
    }

    @Test
    void 위클리_반복_뚜두의_뚜두를_생성한다() {
      // given
      List<String> repeatDayOfWeek = RepeatDduduFixture.getRandomRepeatDaysOfWeek(1);
      RepeatDdudu weeklyRepeatDdudu = RepeatDduduFixture.createRepeatDdudu(
          RepeatType.WEEKLY,
          RepeatDduduFixture.createWeeklyRepeatPattern(repeatDayOfWeek),
          startDate,
          endDate
      );

      // when
      List<Ddudu> ddudus = repeatDduduDomainService.createRepeatedDdudus(
          userId, weeklyRepeatDdudu);

      // then
      ddudus.stream()
          .map(Ddudu::getScheduledOn)
          .forEach(date -> assertThat(date.getDayOfWeek()
              .name()).isEqualTo(repeatDayOfWeek.get(0)));
    }

    @Test
    void 먼슬리_반복_뚜두의_뚜두를_생성한다() {
      // given
      int repeatDayOfMonth = RepeatDduduFixture.getRandomInt(1, 31);
      RepeatDdudu monthlyRepeatDdudu = RepeatDduduFixture.createRepeatDdudu(
          RepeatType.MONTHLY,
          RepeatDduduFixture.createMonthlyRepeatPattern(List.of(repeatDayOfMonth), true),
          startDate,
          endDate
      );

      // when
      List<Ddudu> ddudus = repeatDduduDomainService.createRepeatedDdudus(
          userId, monthlyRepeatDdudu);

      // then
      ddudus.stream()
          .map(Ddudu::getScheduledOn)
          .forEach(date -> assertThat(date.getDayOfMonth())
              .isIn(repeatDayOfMonth, startDate.lengthOfMonth()));
    }

  }

}
