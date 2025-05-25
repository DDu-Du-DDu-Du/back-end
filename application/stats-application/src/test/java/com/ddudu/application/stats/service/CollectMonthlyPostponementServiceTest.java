package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.common.dto.stats.PostponedPerGoal;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CollectMonthlyPostponementServiceTest {

  @Autowired
  CollectMonthlyPostponementService collectMonthlyPostponementService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  List<Goal> goals;
  YearMonth thisMonth;
  List<Integer> postponedCounts;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
    thisMonth = YearMonth.now();
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

    // when
    GenericStatsResponse<PostponedPerGoal> response = collectMonthlyPostponementService.collectPostponement(
        user.getId(),
        thisMonth
    );

    // then
    List<Integer> actual = response.contents()
        .stream()
        .map(PostponedPerGoal::postponementCount)
        .toList();

    assertThat(actual).isEqualTo(postponedCounts);
  }

  @Test
  void 미루기_뚜두가_없는_달이면_통계는_비어있다() {
    // given
    YearMonth noDduduMonth = thisMonth.minusMonths(2);

    // when
    GenericStatsResponse<PostponedPerGoal> response = collectMonthlyPostponementService.collectPostponement(
        user.getId(),
        noDduduMonth
    );

    // then
    assertThat(response.isEmpty()).isTrue();
  }

  @Test
  void 날짜가_null이면_이번_달_통계를_반환한다() {
    // given

    // when
    GenericStatsResponse<PostponedPerGoal> response = collectMonthlyPostponementService.collectPostponement(
        user.getId(),
        null
    );

    // then
    assertThat(response.contents()).isNotEmpty();
  }

  @Test
  void 존재하지_않는_사용자_ID이면_예외를_던진다() {
    // given
    Long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable collect = () -> collectMonthlyPostponementService.collectPostponement(
        invalidId,
        thisMonth
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(collect)
        .withMessage(StatsErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
