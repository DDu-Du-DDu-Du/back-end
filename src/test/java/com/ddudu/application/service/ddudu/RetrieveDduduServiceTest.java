package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.response.DduduDetailResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
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
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user));
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
        .hasFieldOrPropertyWithValue("endAt", ddudu.getEndAt());

  }

  @Test
  void 유효하지_않은_ID인_경우_조회에_실패한다() {
    // given
    Long invalidId = DduduFixture.getRandomId();

    // when
    ThrowingCallable callable = () -> retrieveDduduService.findById(user.getId(), invalidId);

    // then
    assertThatThrownBy(callable)
        .isInstanceOf(MissingResourceException.class)
        .hasMessageContaining(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_뚜두의_주인이_아닌_경우_조회에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable callable = () -> retrieveDduduService.findById(
        anotherUser.getId(), ddudu.getId());

    // then
    assertThatThrownBy(callable)
        .isInstanceOf(SecurityException.class)
        .hasMessageContaining(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
