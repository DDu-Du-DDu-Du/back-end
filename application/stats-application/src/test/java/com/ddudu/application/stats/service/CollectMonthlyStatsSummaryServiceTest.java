package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.stats.GoalMonthlyStatsSummary;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
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
class CollectMonthlyStatsSummaryServiceTest {

  @Autowired
  private CollectMonthlyStatsSummaryService collectMonthlyStatsSummaryService;

  @Autowired
  private SignUpPort signUpPort;

  @Autowired
  private SaveGoalPort saveGoalPort;

  @Autowired
  private SaveDduduPort saveDduduPort;

  private User loginUser;
  private User targetUser;
  private Goal targetGoal;

  @BeforeEach
  void setUp() {
    loginUser = signUpPort.save(UserFixture.createRandomUserWithId());
    targetUser = signUpPort.save(UserFixture.createRandomUserWithId());

    targetGoal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(targetUser.getId()));

    List<Ddudu> ddudus = List.of(
        DduduFixture.createRandomDduduWithReference(
            targetGoal.getId(),
            targetUser.getId(),
            true,
            DduduStatus.COMPLETE
        ),
        DduduFixture.createRandomDduduWithReference(
            targetGoal.getId(),
            targetUser.getId(),
            true,
            DduduStatus.UNCOMPLETED
        ),
        DduduFixture.createRandomDduduWithReference(
            targetGoal.getId(),
            targetUser.getId(),
            false,
            DduduStatus.COMPLETE
        )
    );

    saveDduduPort.saveAll(ddudus);
  }

  @Test
  void 요청_사용자_아이디가_있으면_로그인_사용자_대신_해당_사용자_기준으로_월_요약을_조회한다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
        loginUser.getId(),
        targetUser.getId(),
        thisMonth
    );

    // then
    assertThat(response.summaries()).hasSize(1);

    GoalMonthlyStatsSummary summary = response.summaries().get(0);
    assertThat(summary.goalId()).isEqualTo(targetGoal.getId());
    assertThat(summary.goalName()).isEqualTo(targetGoal.getName());
    assertThat(summary.goalColor()).isEqualTo(targetGoal.getColor());
    assertThat(summary.creationCount()).isEqualTo(3);
    assertThat(summary.achievementCount()).isEqualTo(2);
    assertThat(summary.postponedCount()).isEqualTo(2);
    assertThat(summary.reattainedCount()).isEqualTo(1);
    assertThat(summary.sustainedCount()).isGreaterThanOrEqualTo(1);
  }

  @Test
  void 조회_대상_월_입력이_null이면_이번_달_요약을_조회한다() {
    // given

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
        loginUser.getId(),
        targetUser.getId(),
        null
    );

    // then
    assertThat(response.summaries()).hasSize(1);
  }

  @Test
  void 요청_사용자와_로그인_사용자_모두_없으면_월_통계_요약_조회에_실패한다() {
    // given
    long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable collect = () -> collectMonthlyStatsSummaryService.collectSummary(
        invalidUserId,
        null,
        YearMonth.now()
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(collect)
        .withMessage(StatsErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
