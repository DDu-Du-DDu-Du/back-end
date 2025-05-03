package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.ddudu.application.planning.ddudu.service.DeleteDduduService;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.ddudu.port.out.DduduLoaderPort;
import com.ddudu.application.planning.ddudu.port.out.SaveDduduPort;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class DeleteDduduServiceTest {

  @Autowired
  DeleteDduduService deleteDduduService;

  @Autowired
  UserLoaderPort userLoaderPort;

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

  User user;
  Ddudu ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
  }

  @Test
  void 뚜두를_삭제_할_수_있다() {
    // when
    deleteDduduService.delete(user.getId(), ddudu.getId());

    // then
    assertThat(dduduLoaderPort.getOptionalDdudu(ddudu.getId())).isEmpty();
  }

  @Test
  void 뚜두가_존재하지_않는_경우_예외가_발생하지_않는다() {
    // given
    Long invalidId = DduduFixture.getRandomId();

    // when / then
    assertDoesNotThrow(() -> deleteDduduService.delete(user.getId(), invalidId));
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable delete = () -> deleteDduduService.delete(anotherUser.getId(), ddudu.getId());

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(delete)
        .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }


}
