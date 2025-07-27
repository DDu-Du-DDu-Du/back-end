package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.stats.CreationCountPerGoal;
import com.ddudu.application.common.dto.stats.PostponedPerGoal;
import com.ddudu.application.common.dto.stats.ReattainmentPerGoal;
import com.ddudu.application.common.dto.stats.SustenancePerGoal;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
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
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CollectMonthlyStatsSummaryServiceTest {

  @Autowired
  CollectMonthlyStatsSummaryService collectMonthlyStatsSummaryService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  List<Goal> goals;
  @Autowired
  private DduduLoaderPort dduduLoaderPort;

  @Nested
  class 만든_수_통계_테스트 {

    List<Integer> sizes;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      sizes = new ArrayList<>();

      for (int i = 0; i < 5; i++) {
        sizes.add(DduduFixture.getRandomInt(1, 100));
      }

      Iterator<Integer> sizeIterator = sizes.iterator();

      goals.forEach(goal -> saveDduduPort.saveAll(DduduFixture.createMultipleDdudusWithGoal(
          goal,
          sizeIterator.next()
      )));

      sizes.sort(Comparator.reverseOrder());
    }

    @Test
    void 이번_달_월별_목표들의_뚜두_생성_수_통계를_내림차순으로_낸다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          thisMonth
      );

      // then
      List<CreationCountPerGoal> actual = response.creationCounts();
      Iterator<Integer> sizeIterator = sizes.iterator();

      Assertions.assertThat(actual.size())
          .isEqualTo(goals.size());
      Assertions.assertThat(actual)
          .allMatch(goal -> goal.count() == sizeIterator.next());
    }

    @Test
    void 목표에_해당_달_생성_뚜두가_없으면_통계에서_제외한다() {
      // given
      YearMonth lastMonth = YearMonth.now()
          .minusMonths(1);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          lastMonth
      );

      // then
      assertThat(response.creationCounts()).isEmpty();
    }

    @Test
    void 날짜가_없으면_이번_달_통계를_낸다() {
      // given

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null
      );

      // then
      List<CreationCountPerGoal> actual = response.creationCounts();
      Iterator<Integer> sizeIterator = sizes.iterator();

      Assertions.assertThat(actual.size())
          .isEqualTo(goals.size());
      Assertions.assertThat(actual)
          .allMatch(goal -> goal.count() == sizeIterator.next());
    }

  }

  @Nested
  class 달성도_통계_테스트 {

    List<Integer> sizes;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      sizes = new ArrayList<>();

      for (int i = 0; i < 5; i++) {
        sizes.add(DduduFixture.getRandomInt(1, 100));
      }

      Iterator<Integer> sizeIterator = sizes.iterator();

      goals.forEach(goal -> saveDduduPort.saveAll(DduduFixture.createMultipleDdudusWithGoal(
          goal,
          sizeIterator.next()
      )));

      sizes.sort(Comparator.reverseOrder());
    }

    @Test
    void 이번_달_목표별_뚜두_달성률_통계를_내림차순으로_반환한다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          thisMonth
      );

      // then
      assertThat(response.achievements()).hasSize(goals.size());
    }

    @Test
    void 목표에_해당_달의_뚜두_데이터가_없으면_통계에_포함되지_않는다() {
      // given
      YearMonth lastMonth = YearMonth.now()
          .minusMonths(1);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          lastMonth
      );

      // then
      assertThat(response.achievements()).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    void 날짜가_null이면_이번_달_통계를_반환한다(YearMonth yearMonth) {
      // given

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          yearMonth
      );

      // then
      assertThat(response.achievements()).hasSize(goals.size());
    }

  }

  @Nested
  class 지속도_통계_테스트 {

    List<Integer> expectedSustenanceCount;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      expectedSustenanceCount = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
        expectedSustenanceCount.add(MonthlyStatsFixture.getRandomInt(1, 10));
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
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          thisMonth
      );

      // then
      List<SustenancePerGoal> actual = response.sustenances();
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
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          lastMonth
      );

      // then
      assertThat(response.sustenances()).isEmpty();
    }

    @Test
    void 날짜가_null이면_이번_달_통계를_반환한다() {
      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null
      );

      // then
      assertThat(response.sustenances()).hasSameSizeAs(goals);
    }

  }

  @Nested
  class 미루기_통계_테스트 {

    List<Integer> postponedCounts;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      postponedCounts = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
        postponedCounts.add(DduduFixture.getRandomInt(0, 100));
      }

      Iterator<Integer> iterator = postponedCounts.iterator();

      goals.forEach(goal -> saveDduduPort.saveAll(DduduFixture.createDdudusWithPostponedFlag(
          goal,
          iterator.next(),
          DduduFixture.getRandomInt(1, 100)
      )));

      postponedCounts.sort(Comparator.reverseOrder());
    }

    @Test
    void 월_목표_별_미루기_통계를_반환한다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          thisMonth
      );

      // then
      List<Integer> actual = response.postponements()
          .stream()
          .map(PostponedPerGoal::postponementCount)
          .toList();

      assertThat(actual).isEqualTo(postponedCounts);
    }

    @Test
    void 미루기_뚜두가_없는_달이면_통계는_비어있다() {
      // given
      YearMonth noDduduMonth = YearMonth.now()
          .minusMonths(2);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          noDduduMonth
      );

      // then
      assertThat(response.postponements()).isEmpty();
    }

    @Test
    void 날짜가_null이면_이번_달_통계를_반환한다() {
      // given

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null
      );

      // then
      assertThat(response.postponements()).isNotEmpty();
    }

  }

  @Nested
  class 재달성률_통계_테스트 {

    List<Integer> sizes;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      sizes = new ArrayList<>();
      List<Integer> reattainmentRates = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
        sizes.add(DduduFixture.getRandomInt(1, 100));
      }

      for (int i = 0; i < goals.size(); i++) {
        int totalPostponed = sizes.get(i);
        int reattainment = MonthlyStatsFixture.getRandomInt(1, totalPostponed);

        saveDduduPort.saveAll(DduduFixture.createReattainedDdudus(
            goals.get(i),
            reattainment,
            totalPostponed
        ));

        reattainmentRates.add(Math.round((float) reattainment / totalPostponed * 100));
      }

      reattainmentRates.sort(Comparator.reverseOrder());

      sizes = reattainmentRates;
    }

    @Test
    void 월_목표_별_재달성률_통계를_반환한다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          thisMonth
      );
      List<Integer> actual = response.reattainments()
          .stream()
          .map(ReattainmentPerGoal::reattainmentRate)
          .toList();

      // then
      assertThat(actual).isEqualTo(sizes);
    }

    @Test
    void 목표에_해당_월_지속한_뚜두가_없으면_통계에서_제외한다() {
      // given
      YearMonth lastMonth = YearMonth.now()
          .minusMonths(1);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          lastMonth
      );

      // then
      assertThat(response.reattainments()).isEmpty();
    }

    @Test
    void 날짜가_null이면_이번_달_통계를_반환한다() {
      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null
      );

      // then
      assertThat(response.reattainments()).hasSameSizeAs(goals);
    }

  }

  @Test
  void 로그인_사용자가_없으면_월_통계_계산을_실패한다() {
    // given
    long invalidId = GoalFixture.getRandomId();
    YearMonth thisMonth = YearMonth.now();

    // when
    ThrowingCallable collect = () -> collectMonthlyStatsSummaryService.collectSummary(
        invalidId,
        thisMonth
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(collect)
        .withMessage(StatsErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}