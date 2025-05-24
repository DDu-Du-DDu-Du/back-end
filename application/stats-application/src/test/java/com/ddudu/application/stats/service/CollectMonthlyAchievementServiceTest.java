package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.stats.AchievementPerGoal;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CollectMonthlyAchievementServiceTest {

  @Autowired
  CollectMonthlyAchievementService collectMonthlyAchievementService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  List<Goal> goals;
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
    GenericStatsResponse<AchievementPerGoal> response = collectMonthlyAchievementService.collectAchievement(
        user.getId(),
        thisMonth
    );

    // then
    assertThat(response.contents()).hasSize(goals.size());
  }

  @Test
  void 목표에_해당_달의_뚜두_데이터가_없으면_통계에_포함되지_않는다() {
    // given
    YearMonth lastMonth = YearMonth.now()
        .minusMonths(1);

    // when
    GenericStatsResponse<AchievementPerGoal> response = collectMonthlyAchievementService.collectAchievement(
        user.getId(),
        lastMonth
    );

    // then
    assertThat(response.isEmpty()).isTrue();
  }

  @ParameterizedTest
  @NullSource
  void 날짜가_null이면_이번_달_통계를_반환한다(YearMonth yearMonth) {
    // given

    // when
    GenericStatsResponse<AchievementPerGoal> response = collectMonthlyAchievementService.collectAchievement(
        user.getId(),
        yearMonth
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
    ThrowingCallable collect = () -> collectMonthlyAchievementService.collectAchievement(
        invalidId,
        thisMonth
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(collect)
        .withMessage(StatsErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
