package com.ddudu.application.planning.repeatddudu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeatddudu.exception.RepeatDduduErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.repeatddudu.dto.request.CreateRepeatDduduRequest;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.ddudu.port.out.DduduLoaderPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.application.planning.repeatddudu.port.out.RepeatDduduLoaderPort;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatDduduFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
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
class CreateRepeatDduduServiceTest {

  @Autowired
  CreateRepeatDduduService createRepeatDduduService;
  @Autowired
  RepeatDduduLoaderPort repeatDduduLoaderPort;
  @Autowired
  DduduLoaderPort dduduLoaderPort;
  @Autowired
  SignUpPort signUpPort;
  @Autowired
  SaveGoalPort saveGoalPort;

  User user;
  Goal goal;
  String name;
  LocalDate startDate;
  LocalDate endDate;
  RepeatType repeatType;
  List<String> repeatDaysOfWeek;
  List<Integer> repeatDaysOfMonth;
  Boolean lastDayOfMonth;
  CreateRepeatDduduRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user));
    name = RepeatDduduFixture.getRandomSentenceWithMax(50);
    repeatType = RepeatDduduFixture.getRandomRepeatType();
    repeatDaysOfWeek = RepeatDduduFixture.getRandomRepeatDaysOfWeek();
    repeatDaysOfMonth = RepeatDduduFixture.getRandomRepeatDaysOfMonth();
    lastDayOfMonth = true;
    startDate = LocalDate.now();
    endDate = LocalDate.now()
        .plusMonths(1);
    request = new CreateRepeatDduduRequest(
        name,
        goal.getId(),
        repeatType.name(),
        repeatDaysOfWeek,
        repeatDaysOfMonth,
        lastDayOfMonth,
        startDate,
        endDate,
        null,
        null
    );
  }

  @Test
  void 반복_뚜두_생성에_성공한다() {
    // when
    Long repeatDduduId = createRepeatDduduService.create(user.getId(), request);

    // then
    RepeatDdudu repeatDdudu = repeatDduduLoaderPort.getOptionalRepeatDdudu(repeatDduduId)
        .get();
    assertThat(repeatDdudu)
        .hasFieldOrPropertyWithValue("name", name)
        .hasFieldOrPropertyWithValue("goalId", goal.getId())
        .hasFieldOrPropertyWithValue("repeatType", repeatType)
        .hasFieldOrPropertyWithValue("startDate", startDate)
        .hasFieldOrPropertyWithValue("endDate", endDate);
  }

  @Test
  void 반복_뚜두_생성_시_뚜두도_함께_생성된다() {
    // when
    Long repeatDduduId = createRepeatDduduService.create(user.getId(), request);

    // then
    RepeatDdudu repeatDdudu = repeatDduduLoaderPort.getOptionalRepeatDdudu(repeatDduduId)
        .get();
    List<Ddudu> ddudus = dduduLoaderPort.getRepeatedDdudus(repeatDdudu);
    Assertions.assertThat(ddudus).isNotEmpty();
  }

  @Test
  void 목표_아이디가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidGoalId = GoalFixture.getRandomId();
    CreateRepeatDduduRequest request = new CreateRepeatDduduRequest(
        name,
        invalidGoalId,
        repeatType.name(),
        repeatDaysOfWeek,
        repeatDaysOfMonth,
        lastDayOfMonth,
        startDate,
        endDate,
        null,
        null
    );

    // when
    ThrowingCallable create = () -> createRepeatDduduService.create(user.getId(), request);

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class).isThrownBy(create)
        .withMessage(RepeatDduduErrorCode.INVALID_GOAL.getCodeName());
  }

  @Test
  void 본인의_목표가_아닌_경우_예외가_발생한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable create = () -> createRepeatDduduService.create(anotherUser.getId(), request);

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(SecurityException.class).isThrownBy(create)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
