package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.UpdateDduduRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalTime;
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
class UpdateDduduServiceTest {

  @Autowired
  UpdateDduduService updateDduduService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  Goal goal;
  Ddudu ddudu;
  UpdateDduduRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
    request = new UpdateDduduRequest(
        goal.getId(),
        DduduFixture.getRandomSentenceWithMax(50),
        DduduFixture.createValidMemo(),
        LocalDate.now().plusDays(1),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        null,
        null,
        null
    );
  }

  @Test
  void 뚜두를_수정한다() {
    // given

    // when
    BasicDduduResponse actual = updateDduduService.update(
        user.getId(),
        ddudu.getId(),
        request
    );

    // then
    assertThat(actual.id()).isEqualTo(ddudu.getId());
    assertThat(actual.name()).isEqualTo(request.name());
  }

  @Test
  void 로그인_사용자가_없으면_수정에_실패한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateDduduService.update(
        invalidUserId,
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 존재하지_않는_뚜두면_수정에_실패한다() {
    // given
    Long invalidDduduId = DduduFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateDduduService.update(
        user.getId(),
        invalidDduduId,
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 작성자가_아니면_수정에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable update = () -> updateDduduService.update(
        anotherUser.getId(),
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(SecurityException.class)
        .hasMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
