package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.ChangeNameRequest;
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
class ChangeNameServiceTest {

  @Autowired
  ChangeNameService changeNameService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  Goal goal;
  Ddudu ddudu;
  ChangeNameRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
    request = new ChangeNameRequest(DduduFixture.getRandomSentenceWithMax(50));
  }

  @Test
  void 뚜두의_이름을_변경한다() {
    // when
    BasicDduduResponse actual = changeNameService.change(user.getId(), ddudu.getId(), request);

    // then
    assertThat(actual.name()).isEqualTo(request.name());
  }

  @Test
  void 변경할_이름이_50자가_넘으면_변경에_실패한다() {
    // given
    request = new ChangeNameRequest(DduduFixture.getRandomSentence(51, 100));

    // when
    ThrowingCallable changeName = () -> changeNameService.change(
        user.getId(),
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(changeName)
        .withMessage(DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
  }

  @Test
  void 존재하지_않는_뚜두인_경우_변경에_실패한다() {
    // given
    Long invalidId = DduduFixture.getRandomId();

    // when
    ThrowingCallable changeName = () -> changeNameService.change(user.getId(), invalidId, request);

    // then
    Assertions.assertThatThrownBy(changeName)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 뚜두를_생성한_사용자가_아닌_경우_변경에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable changeName = () -> changeNameService.change(
        anotherUser.getId(),
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(changeName)
        .isInstanceOf(SecurityException.class)
        .hasMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
