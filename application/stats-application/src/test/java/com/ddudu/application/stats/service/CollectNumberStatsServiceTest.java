package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CollectNumberStatsServiceTest {

  @Autowired
  CollectMonthlyStatsSummaryService collectMonthlyStatsService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  List<Goal> goals;
  int uncompletedCountPerGoal;
  int completedCountPerGoal;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
    uncompletedCountPerGoal = 5;
    completedCountPerGoal = 5;
    LocalDate lastMonth = LocalDate.now()
        .minusMonths(1);

    goals.forEach(goal -> saveDduduPort.saveAll(
        DduduFixture.createDifferentDdudusWithGoal(
            goal, completedCountPerGoal,
            uncompletedCountPerGoal
        )));

    for (int i = 0; i < completedCountPerGoal; i++) {
      goals.forEach(goal -> saveDduduPort.save(
          DduduFixture.createRandomDduduWithStatusAndSchedule(
              goal, DduduStatus.COMPLETE,
              lastMonth
          )
      ));
    }

    for (int i = 0; i < uncompletedCountPerGoal; i++) {
      goals.forEach(goal -> saveDduduPort.save(
          DduduFixture.createRandomDduduWithStatusAndSchedule(
              goal, DduduStatus.UNCOMPLETED,
              lastMonth
          )
      ));
    }
  }

  @Test
  void 이번달_뚜두_통합_통계를_낸다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsService.collectMonthlyTotalStats(
        user.getId(), thisMonth);

    // then
    int totalPerGoal = uncompletedCountPerGoal + completedCountPerGoal;

    assertThat(response.lastMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth.minusMonths(1))
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
    assertThat(response.thisMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth)
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
  }

  @Test
  void 요청의_날짜가_없으면_이번달의_뚜두_통계를_낸다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsService.collectMonthlyTotalStats(
        user.getId(),
        null
    );

    // then
    int totalPerGoal = uncompletedCountPerGoal + completedCountPerGoal;

    assertThat(response.lastMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth.minusMonths(1))
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
    assertThat(response.thisMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth)
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
  }

  @Test
  void 지난달_뚜두가_없어도_통계는_0으로_계산된다() {
    // given
    YearMonth lastMonth = YearMonth.now()
        .minusMonths(1);

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsService.collectMonthlyTotalStats(
        user.getId(),
        lastMonth
    );

    // then
    assertThat(response.lastMonth()
        .totalCount()).isZero();
  }

  @Test
  void 로그인_사용자가_없으면_월_달성_뚜두_수_통계를_실패한다() {
    // given
    long invalidId = GoalFixture.getRandomId();
    YearMonth thisMonth = YearMonth.now();

    // when
    ThrowingCallable collect = () -> collectMonthlyStatsService.collectMonthlyTotalStats(
        invalidId, thisMonth);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(collect)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}