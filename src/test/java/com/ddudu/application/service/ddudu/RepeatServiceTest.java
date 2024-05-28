package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.dto.ddudu.response.RepeatAnotherDayResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
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
