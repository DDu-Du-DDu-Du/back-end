package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.application.common.dto.stats.response.GoalDetailStatsSummaryResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
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
class CollectGoalDetailStatsServiceTest {

  @Autowired
  CollectGoalDetailStatsService collectGoalDetailStatsService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;
  Goal goal;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
  }

  @Test
  void 목표_상세_통계_요약을_성공한다() {
    // given
    saveTodoPort.saveAll(TodoFixture.createDifferentTodosWithGoal(goal, 3, 2));

    // when
    GoalDetailStatsSummaryResponse response = collectGoalDetailStatsService.collectDetail(
        user.getId(),
        goal.getId(),
        null
    );

    // then
    assertThat(response.id()).isEqualTo(goal.getId());
    assertThat(response.name()).isEqualTo(goal.getName());
    assertThat(response.createdAt()).isNotNull();
    assertThat(response.totalCount()).isEqualTo(5);
    assertThat(response.completedCount()).isEqualTo(3);
    assertThat(response.completeRate()).isEqualTo(60);
  }

  @Test
  void 생성된_투두가_없으면_0으로_채워_반환한다() {
    // given

    // when
    GoalDetailStatsSummaryResponse response = collectGoalDetailStatsService.collectDetail(
        user.getId(),
        goal.getId(),
        null
    );

    // then
    assertThat(response.totalCount()).isZero();
    assertThat(response.completedCount()).isZero();
    assertThat(response.completeRate()).isZero();
  }

  @Test
  void 사용자가_존재하지_않으면_예외가_발생한다() {
    // given
    long invalidUserId = Long.MAX_VALUE;

    // when
    ThrowingCallable when = () -> collectGoalDetailStatsService.collectDetail(
        invalidUserId,
        goal.getId(),
        invalidUserId
    );

    // then
    assertThatThrownBy(when)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(StatsErrorCode.USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 목표가_존재하지_않으면_예외가_발생한다() {
    // given
    long invalidGoalId = Long.MAX_VALUE;

    // when
    ThrowingCallable when = () -> collectGoalDetailStatsService.collectDetail(
        user.getId(),
        invalidGoalId,
        null
    );

    // then
    assertThatThrownBy(when)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(StatsErrorCode.GOAL_NOT_EXISTING.getCodeName());
  }

}
