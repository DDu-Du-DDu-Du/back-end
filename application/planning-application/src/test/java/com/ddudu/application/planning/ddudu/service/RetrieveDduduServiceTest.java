package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.response.DduduDetailResponse;
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
import org.assertj.core.api.AssertionsForClassTypes;
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
class RetrieveDduduServiceTest {

  @Autowired
  RetrieveDduduService retrieveDduduService;

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
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
  }

  @Test
  void ID를_통해_뚜두를_조회할_수_있다() {
    // when
    DduduDetailResponse actual = retrieveDduduService.findById(user.getId(), ddudu.getId());

    // then
    assertThat(actual)
        .hasFieldOrPropertyWithValue("id", ddudu.getId())
        .hasFieldOrPropertyWithValue("name", ddudu.getName())
        .hasFieldOrPropertyWithValue("status", ddudu.getStatus())
        .hasFieldOrPropertyWithValue("goalId", ddudu.getGoalId())
        .hasFieldOrPropertyWithValue("repeatDduduId", ddudu.getRepeatDduduId())
        .hasFieldOrPropertyWithValue("scheduledOn", ddudu.getScheduledOn())
        .hasFieldOrPropertyWithValue("beginAt", ddudu.getBeginAt())
        .hasFieldOrPropertyWithValue("endAt", ddudu.getEndAt())
        .hasFieldOrPropertyWithValue("remindAt", ddudu.getRemindAt());

  }

  @Test
  void 유효하지_않은_ID인_경우_조회에_실패한다() {
    // given
    Long invalidId = DduduFixture.getRandomId();

    // when
    ThrowingCallable callable = () -> retrieveDduduService.findById(user.getId(), invalidId);

    // then
    AssertionsForClassTypes.assertThatThrownBy(callable)
        .isInstanceOf(MissingResourceException.class)
        .hasMessageContaining(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_뚜두의_주인이_아닌_경우_조회에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable callable = () -> retrieveDduduService.findById(
        anotherUser.getId(),
        ddudu.getId()
    );

    // then
    AssertionsForClassTypes.assertThatThrownBy(callable)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
