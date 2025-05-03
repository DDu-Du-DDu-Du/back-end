package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.planning.ddudu.service.RepeatService;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.ddudu.dto.request.RepeatAnotherDayRequest;
import com.ddudu.application.planning.ddudu.dto.response.RepeatAnotherDayResponse;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.ddudu.port.out.DduduLoaderPort;
import com.ddudu.application.planning.ddudu.port.out.SaveDduduPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
class RepeatServiceTest {

  @Autowired
  RepeatService repeatService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

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
  void 뚜두_다른_날_반복하기를_성공한다() {
    // given
    LocalDate tomorrow = LocalDate.now()
        .plusDays(1);
    RepeatAnotherDayRequest request = new RepeatAnotherDayRequest(tomorrow);

    // when
    RepeatAnotherDayResponse response = repeatService.repeatOnAnotherDay(
        user.getId(), ddudu.getId(), request);

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(response.id(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(tomorrow);
    assertThat(actual).isNotEqualTo(ddudu);
  }

  @Test
  void 뚜두가_없으면_다른_날_반복하기를_실패한다() {
    // given
    long invalidId = DduduFixture.getRandomId();
    LocalDate tomorrow = LocalDate.now()
        .plusDays(1);
    RepeatAnotherDayRequest request = new RepeatAnotherDayRequest(tomorrow);

    // when
    ThrowingCallable repeat = () -> repeatService.repeatOnAnotherDay(
        user.getId(), invalidId, request);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(repeat)
        .withMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}
