package com.ddudu.application.planning.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.util.Optional;
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
class DeleteGoalServiceTest {

  @Autowired
  DeleteGoalService deleteGoalService;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  Long userId;
  Goal goal;

  @BeforeEach
  void setUp() {
    User user = signUpPort.save(UserFixture.createRandomUserWithId());
    userId = user.getId();
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(userId));
  }

  @Test
  void 목표를_삭제_할_수_있다() {
    // when
    deleteGoalService.delete(userId, goal.getId());

    // then
    Optional<Goal> foundAfterDeleted = goalLoaderPort.getOptionalGoal(goal.getId());

    Assertions.assertThat(foundAfterDeleted)
        .isEmpty();
  }

  @Test
  void 목표_삭제_시_해당_목표의_뚜두도_삭제된다() {
    //given
    Ddudu ddudu = DduduFixture.createRandomDduduWithGoal(goal);
    ddudu = saveDduduPort.save(ddudu);

    //when
    deleteGoalService.delete(userId, goal.getId());

    //then
    assertThat(dduduLoaderPort.getOptionalDdudu(ddudu.getId())).isEmpty();
  }

  @Test
  void 목표가_존재하지_않는_경우_예외가_발생하지_않는다() {
    // given
    Long invalidId = GoalFixture.getRandomId();

    // when / then
    assertDoesNotThrow(() -> deleteGoalService.delete(userId, invalidId));
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable delete = () -> deleteGoalService.delete(anotherUser.getId(), goal.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(delete)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }


}
