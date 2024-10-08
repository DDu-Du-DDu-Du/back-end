package com.ddudu.application.service.repeat_ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.repeat_ddudu.DeleteRepeatDduduPort;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.SaveRepeatDduduPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
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
