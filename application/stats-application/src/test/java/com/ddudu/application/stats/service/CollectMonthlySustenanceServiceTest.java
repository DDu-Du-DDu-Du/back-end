package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.stats.SustenancePerGoal;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import com.ddudu.fixtures.MonthlyStatsFixture;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
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
class CollectMonthlySustenanceServiceTest {

  @Autowired
  CollectMonthlySustenanceService collectMonthlySustenanceService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  List<Goal> goals;
  List<Integer> expectedSustenanceCount;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
    expectedSustenanceCount = new ArrayList<>();

    for (int i = 0; i < goals.size(); i++) {
      expectedSustenanceCount.add(MonthlyStatsFixture.getRandomInt(0, 10));
    }

    Iterator<Integer> iterator = expectedSustenanceCount.iterator();

    goals.forEach(goal -> saveDduduPort.saveAll(DduduFixture.createConsecutiveCompletedDdudus(
        goal,
        iterator.next()
    )));

    expectedSustenanceCount.sort(Comparator.comparingInt(Integer::intValue)
        .reversed());
  }

  @Test
  void 이번_달_지속한_뚜두_수_통계를_내림차순으로_반환한다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    GenericStatsResponse<SustenancePerGoal> response = collectMonthlySustenanceService.collectSustenanceCount(
        user.getId(),
        thisMonth
    );

    // then
    List<SustenancePerGoal> actual = response.contents();
    List<Integer> actualSustenanceCounts = actual.stream()
        .map(SustenancePerGoal::sustenanceCount)
        .toList();

    assertThat(actualSustenanceCounts).isEqualTo(expectedSustenanceCount);
  }

  @Test
  void 목표에_해당_월_지속한_뚜두가_없으면_통계에서_제외한다() {
    // given
    YearMonth lastMonth = YearMonth.now()
        .minusMonths(1);

    // when
    GenericStatsResponse<SustenancePerGoal> response = collectMonthlySustenanceService.collectSustenanceCount(
        user.getId(),
        lastMonth
    );

    // then
    assertThat(response.isEmpty()).isTrue();
  }

  @Test
  void 날짜가_null이면_이번_달_통계를_반환한다() {
    // when
    GenericStatsResponse<SustenancePerGoal> response = collectMonthlySustenanceService.collectSustenanceCount(
        user.getId(),
        null
    );

    // then
    assertThat(response.contents()).hasSize(goals.size());
  }

  @Test
  void 존재하지_않는_사용자_ID이면_예외를_던진다() {
    // given
    long invalidId = GoalFixture.getRandomId();
    YearMonth thisMonth = YearMonth.now();

    // when
    ThrowingCallable call = () -> collectMonthlySustenanceService.collectSustenanceCount(
        invalidId,
        thisMonth
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(call)
        .withMessage(StatsErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}
