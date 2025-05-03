package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.stats.service.CollectMonthlyCreationStatsService;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.stats.dto.CompletionPerGoalDto;
import com.ddudu.application.stats.dto.response.MonthlyStatsResponse;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.ddudu.port.out.SaveDduduPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
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
class CollectMonthlyCreationStatsServiceTest {

  @Autowired
  CollectMonthlyCreationStatsService collectMonthlyCreationStatsService;

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
    goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user, 5));
    sizes = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      sizes.add(DduduFixture.getRandomInt(1, 100));
    }

    Iterator<Integer> sizeIterator = sizes.iterator();

    goals.forEach(goal -> saveDduduPort.saveAll(
        DduduFixture.createMultipleDdudusWithGoal(goal, sizeIterator.next())));

    sizes.sort(Comparator.reverseOrder());
  }

  @Test
  void 이번_달_월별_목표들의_뚜두_생성_수_통계를_내림차순으로_낸다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    MonthlyStatsResponse<CompletionPerGoalDto> response = collectMonthlyCreationStatsService.collectCreation(
        user.getId(), thisMonth);

    // then
    List<CompletionPerGoalDto> actual = response.contents();
    Iterator<Integer> sizeIterator = sizes.iterator();

    assertThat(actual.size()).isEqualTo(goals.size());
    assertThat(actual).allMatch(goal -> goal.count() == sizeIterator.next());
  }

  @Test
  void 목표에_해당_달_생성_뚜두가_없으면_통계에서_제외한다() {
    // given
    YearMonth lastMonth = YearMonth.now()
        .minusMonths(1);

    // when
    MonthlyStatsResponse<CompletionPerGoalDto> response = collectMonthlyCreationStatsService.collectCreation(
        user.getId(), lastMonth);

    // then
    assertThat(response.isEmpty()).isTrue();
  }

  @Test
  void 날짜가_없으면_이번_달_통계를_낸다() {
    // given

    // when
    MonthlyStatsResponse<CompletionPerGoalDto> response = collectMonthlyCreationStatsService.collectCreation(
        user.getId(), null);

    // then
    List<CompletionPerGoalDto> actual = response.contents();
    Iterator<Integer> sizeIterator = sizes.iterator();

    assertThat(actual.size()).isEqualTo(goals.size());
    assertThat(actual).allMatch(goal -> goal.count() == sizeIterator.next());
  }

  @Test
  void 로그인_사용자가_없으면_월_달성_뚜두_수_통계를_실패한다() {
    // given
    long invalidId = GoalFixture.getRandomId();
    YearMonth thisMonth = YearMonth.now();

    // when
    ThrowingCallable collect = () -> collectMonthlyCreationStatsService.collectCreation(
        invalidId, thisMonth);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(collect)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}