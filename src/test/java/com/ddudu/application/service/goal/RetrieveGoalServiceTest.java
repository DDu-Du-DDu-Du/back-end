package com.ddudu.application.service.goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.planning.goal.service.RetrieveGoalService;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.goal.dto.response.GoalWithRepeatDduduResponse;
import com.ddudu.application.planning.repeatddudu.dto.RepeatDduduDto;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.application.planning.repeatddudu.port.out.RepeatDduduLoaderPort;
import com.ddudu.application.planning.repeatddudu.port.out.SaveRepeatDduduPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatDduduFixture;
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
class RetrieveGoalServiceTest {

  @Autowired
  RetrieveGoalService retrieveGoalService;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveRepeatDduduPort saveRepeatDduduPort;

  @Autowired
  RepeatDduduLoaderPort repeatDduduLoaderPort;

  Long userId;
  Goal goal;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goal = createAndSaveGoal(user);
  }

  @Test
  void ID를_통해_목표를_조회_할_수_있다() {
    // when
    GoalWithRepeatDduduResponse actual = retrieveGoalService.getById(userId, goal.getId());

    // then
    assertThat(actual).extracting("id", "name", "status", "color", "privacyType")
        .containsExactly(
            goal.getId(), goal.getName(), goal.getStatus(), goal.getColor(), goal.getPrivacyType());
  }

  @Test
  void 목표_조회_시_해당_목표의_반복_뚜두도_함께_조회된다() {
    // given
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now()
        .plusMonths(1);
    RepeatDdudu repeatDdudu = RepeatDduduFixture.createRepeatDduduWithGoal(
        goal, startDate, endDate);

    saveRepeatDduduPort.save(repeatDdudu);

    // when
    GoalWithRepeatDduduResponse response = retrieveGoalService.getById(userId, goal.getId());

    // then
    RepeatDduduDto first = response.repeatDdudus()
        .get(0);
    RepeatDdudu actual = repeatDduduLoaderPort.getOptionalRepeatDdudu(first.id())
        .get();
    assertThat(actual).extracting("name", "repeatType", "repeatPattern", "startDate", "endDate")
        .containsExactly(
            repeatDdudu.getName(), repeatDdudu.getRepeatType(), repeatDdudu.getRepeatPattern(),
            repeatDdudu.getStartDate(), repeatDdudu.getEndDate()
        );
  }

  @Test
  void 유효하지_않은_ID인_경우_조회에_실패한다() {
    // given
    Long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable getById = () -> retrieveGoalService.getById(userId, invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(getById)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_목표의_주인이_아닌_경우_조회에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable getById = () -> retrieveGoalService.getById(anotherUser.getId(), goal.getId());

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(getById)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  private User createAndSaveUser() {
    User user = UserFixture.createRandomUserWithId();
    return signUpPort.save(user);
  }

  private Goal createAndSaveGoal(User user) {
    Goal goal = GoalFixture.createRandomGoalWithUser(user);
    return saveGoalPort.save(goal);
  }

}
