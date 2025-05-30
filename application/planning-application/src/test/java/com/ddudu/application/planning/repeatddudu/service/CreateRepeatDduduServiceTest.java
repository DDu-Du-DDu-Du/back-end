package com.ddudu.application.planning.repeatddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.repeatddudu.request.CreateRepeatDduduRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.repeatddudu.out.RepeatDduduLoaderPort;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.common.exception.RepeatDduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatDduduFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
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
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    name = RepeatDduduFixture.getRandomSentenceWithMax(50);
    startDate = LocalDate.now();
    endDate = LocalDate.now()
        .plusMonths(1);
    repeatType = RepeatDduduFixture.getRandomRepeatType();
    repeatDaysOfWeek = RepeatDduduFixture.getRandomRepeatDaysOfWeek();
    repeatDaysOfMonth = RepeatDduduFixture.getRandomRepeatDaysOfMonth(
        startDate.getDayOfMonth(),
        YearMonth.now()
            .atEndOfMonth()
            .getDayOfMonth()
    );
    lastDayOfMonth = true;
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

    Assertions.assertThat(ddudus)
        .isNotEmpty();
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
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(RepeatDduduErrorCode.INVALID_GOAL.getCodeName());
  }

  @Test
  void 본인의_목표가_아닌_경우_예외가_발생한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable create = () -> createRepeatDduduService.create(anotherUser.getId(), request);

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(create)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
