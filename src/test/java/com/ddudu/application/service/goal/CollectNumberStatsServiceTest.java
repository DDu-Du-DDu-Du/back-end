package com.ddudu.application.service.goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.response.CompletedDduduNumberStatsResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.YearMonth;
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
class CollectNumberStatsServiceTest {

  @Autowired
  CollectNumberStatsService collectNumberStatsService;

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
    goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user, 5));
    uncompletedCountPerGoal = 5;
    completedCountPerGoal = 5;
    goals.forEach(goal -> saveDduduPort.saveAll(
        DduduFixture.createDifferentDdudusWithGoal(goal, completedCountPerGoal,
            uncompletedCountPerGoal
        )));
  }

  @Test
  void 이번달_달성_뚜두_수의_통계를_낸다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    List<CompletedDduduNumberStatsResponse> response = collectNumberStatsService.collectNumberStats(
        user.getId(), thisMonth);

    // then
    assertThat(response).hasSize(goals.size());
    assertThat(response).allMatch(stats -> stats.completedCount()
        .equals(completedCountPerGoal));
  }

  @Test
  void 요청의_날짜가_없으면_이번달의_달성_뚜두_수의_통계를_낸다() {
    // given

    // when
    List<CompletedDduduNumberStatsResponse> response = collectNumberStatsService.collectNumberStats(
        user.getId(), null);

    // then
    assertThat(response).hasSize(goals.size());
    assertThat(response).allMatch(stats -> stats.completedCount()
        .equals(completedCountPerGoal));
  }

  @Test
  void 이번달_달성_뚜두_수의_통계는_내림차순이다() {
    // given
    YearMonth thisMonth = YearMonth.now();
    int extraForThird = 3;
    int extraForSecond = 2;
    Goal thirdGoal = goals.get(2);
    Goal secondGoal = goals.get(1);

    saveDduduPort.saveAll(DduduFixture.createMultipleDdudusWithGoal(thirdGoal, extraForThird));
    saveDduduPort.saveAll(DduduFixture.createMultipleDdudusWithGoal(secondGoal, extraForSecond));

    // when
    List<CompletedDduduNumberStatsResponse> response = collectNumberStatsService.collectNumberStats(
        user.getId(), thisMonth);

    // then
    assertThat(response.get(0))
        .hasFieldOrPropertyWithValue("id", thirdGoal.getId())
        .hasFieldOrPropertyWithValue("completedCount", completedCountPerGoal + extraForThird);
    assertThat(response.get(1))
        .hasFieldOrPropertyWithValue("id", secondGoal.getId())
        .hasFieldOrPropertyWithValue("completedCount", completedCountPerGoal + extraForSecond);
  }

  @Test
  void 통계_대상_월에_뚜두를_생성하지_않은_목표는_통계에서_제외된다() {
    // given
    YearMonth nextMonth = YearMonth.now()
        .plusMonths(1);

    // when
    List<CompletedDduduNumberStatsResponse> response = collectNumberStatsService.collectNumberStats(
        user.getId(), nextMonth);

    // then
    assertThat(response).isEmpty();
  }

  @Test
  void 통계_대상_월에_뚜두를_생성했지만_달성한_뚜두가_없으면_0개의_통계를_낸다() {
    // given
    YearMonth nextMonth = YearMonth.now()
        .plusMonths(1);
    Goal firstGoal = goals.get(0);
    Ddudu extra = saveDduduPort.save(
        DduduFixture.createRandomDduduWithStatusAndSchedule(firstGoal, DduduStatus.UNCOMPLETED,
            LocalDate.now()
                .plusMonths(1)
        ));

    // when
    List<CompletedDduduNumberStatsResponse> response = collectNumberStatsService.collectNumberStats(
        user.getId(), nextMonth);

    // then
    assertThat(response).hasSize(1);
    assertThat(response).allMatch(stats -> stats.completedCount()
        .equals(0));
  }

  @Test
  void 로그인_사용자가_없으면_월_달성_뚜두_수_통계를_실패한다() {
    // given
    long invalidId = GoalFixture.getRandomId();
    YearMonth thisMonth = YearMonth.now();

    // when
    ThrowingCallable collect = () -> collectNumberStatsService.collectNumberStats(
        invalidId, thisMonth);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(collect)
        .withMessage(GoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}