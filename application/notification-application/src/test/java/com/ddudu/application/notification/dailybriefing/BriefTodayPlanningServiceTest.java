package com.ddudu.application.notification.dailybriefing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.response.DailyBriefingResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.out.DailyBriefingCommandPort;
import com.ddudu.common.exception.DailyBriefingLogErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DailyBriefingLogFixture;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
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
class BriefTodayPlanningServiceTest {

  @Autowired
  BriefTodayPlanningService briefTodayPlanningService;

  @Autowired
  DailyBriefingCommandPort dailyBriefingCommandPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SignUpPort signUpPort;

  User user;
  int dduduCount;
  List<Ddudu> ddudus;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudus = new ArrayList<>();
    dduduCount = DduduFixture.getRandomInt(1, 10);

    for (int i = 0; i < dduduCount; i++) {
      ddudus.add(DduduFixture.createRandomDduduWithSchedule(
          user.getId(),
          goal.getId(),
          LocalDate.now()
      ));
    }

    saveDduduPort.saveAll(ddudus);
  }

  @Test
  void 오늘_처음_조회하는_경우_뚜두_개수와_함께_응답을_반환한다() {
    // given

    // when
    DailyBriefingResponse actual = briefTodayPlanningService.getDailyBriefing(user.getId());

    // then
    assertThat(actual.isFirst()).isTrue();
    assertThat(actual.content()
        .count()).isEqualTo(dduduCount);
  }

  @Test
  void 이미_오늘_조회한_경우_첫_조회가_아님을_표시한_응답을_반환한다() {
    // given
    dailyBriefingCommandPort.save(DailyBriefingLogFixture.createTodayBriefing(user.getId()));

    // when
    DailyBriefingResponse actual = briefTodayPlanningService.getDailyBriefing(user.getId());

    // then
    assertThat(actual.isFirst()).isFalse();
    assertThat(actual.content()).isNull();
  }

  @Test
  void 존재하지_않는_사용자인_경우_예외가_발생한다() {
    // given
    Long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable brief = () -> briefTodayPlanningService.getDailyBriefing(invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(brief)
        .withMessage(DailyBriefingLogErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}
