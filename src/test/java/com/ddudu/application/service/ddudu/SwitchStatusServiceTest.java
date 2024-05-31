package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.MissingResourceException;
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
class SwitchStatusServiceTest {

  @Autowired
  SwitchStatusService switchStatusService;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;
  
  User user;
  Goal goal;
  Ddudu ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
  }

  @Test
  void 할_일_상태_업데이트를_성공한다() {
    // given
    DduduStatus beforeUpdated = ddudu.getStatus();

    // when
    switchStatusService.switchStatus(user.getId(), ddudu.getId());

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(ddudu.getId(), "할 일이 존재하지 않습니다.");
    assertThat(actual.getStatus()).isNotEqualTo(beforeUpdated);
  }

  @Test
  void 아이디가_존재하지_않아_할_일_상태_업데이트를_실패한다() {
    // given
    Long invalidDduduId = DduduFixture.getRandomId();

    // when
    ThrowingCallable switchStatus = () -> switchStatusService.switchStatus(
        user.getId(), invalidDduduId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(switchStatus)
        .withMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자와_할_일_사용자가_다르면_상태_업데이트_실패한다() {
    // given
    Long anotherUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable updateStatus = () -> switchStatusService.switchStatus(
        anotherUserId, ddudu.getId());

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(updateStatus)
        .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
