package com.ddudu.application.service.repeatddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.ddudu.application.planning.repeatddudu.service.DeleteRepeatDduduService;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.ddudu.port.out.DduduLoaderPort;
import com.ddudu.application.planning.ddudu.port.out.SaveDduduPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.application.planning.repeatddudu.port.out.DeleteRepeatDduduPort;
import com.ddudu.application.planning.repeatddudu.port.out.RepeatDduduLoaderPort;
import com.ddudu.application.planning.repeatddudu.port.out.SaveRepeatDduduPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatDduduFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.Optional;
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
class DeleteRepeatDduduServiceTest {

  @Autowired
  DeleteRepeatDduduService deleteRepeatDduduService;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  RepeatDduduLoaderPort repeatDduduLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveRepeatDduduPort saveRepeatDduduPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  DeleteRepeatDduduPort deleteRepeatDduduPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  Long userId;
  RepeatDdudu repeatDdudu;

  @BeforeEach
  void setUp() {
    User user = signUpPort.save(UserFixture.createRandomUserWithId());
    userId = user.getId();
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user));
    repeatDdudu = saveRepeatDduduPort.save(RepeatDduduFixture.createDailyRepeatDduduWithGoal(goal));
  }

  @Test
  void 반복뚜두를_삭제_할_수_있다() {
    // when
    deleteRepeatDduduService.delete(userId, repeatDdudu.getId());

    // then
    Optional<RepeatDdudu> foundAfterDeleted = repeatDduduLoaderPort.getOptionalRepeatDdudu(
        repeatDdudu.getId());
    assertThat(foundAfterDeleted).isEmpty();
  }

  @Test
  void 반복뚜두_삭제_시_해당_반복뚜두의_뚜두도_삭제된다() {
    //given
    Ddudu ddudu = DduduFixture.createRandomDduduWithRepeatDdudu(userId, repeatDdudu);
    ddudu = saveDduduPort.save(ddudu);

    //when
    deleteRepeatDduduService.delete(userId, repeatDdudu.getId());

    //then
    assertThat(dduduLoaderPort.getOptionalDdudu(ddudu.getId())).isEmpty();
  }

  @Test
  void 반복뚜두가_존재하지_않는_경우_예외가_발생하지_않는다() {
    // given
    Long invalidId = RepeatDduduFixture.getRandomId();

    // when / then
    assertDoesNotThrow(() -> deleteRepeatDduduService.delete(userId, invalidId));
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable delete = () -> deleteRepeatDduduService.delete(
        anotherUser.getId(), repeatDdudu.getId());

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(delete)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
