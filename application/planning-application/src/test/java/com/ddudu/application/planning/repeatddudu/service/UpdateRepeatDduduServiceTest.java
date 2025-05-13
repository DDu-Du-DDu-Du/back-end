package com.ddudu.application.planning.repeatddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.repeatddudu.request.UpdateRepeatDduduRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DduduUpdatePort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.repeatddudu.out.RepeatDduduLoaderPort;
import com.ddudu.application.common.port.repeatddudu.out.SaveRepeatDduduPort;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeatddudu.service.RepeatDduduDomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
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
class UpdateRepeatDduduServiceTest {

  @Autowired
  UpdateRepeatDduduService updateRepeatDduduService;

  @Autowired
  RepeatDduduDomainService repeatDduduDomainService;

  @Autowired
  RepeatDduduLoaderPort repeatDduduLoaderPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveRepeatDduduPort saveRepeatDduduPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  DduduUpdatePort dduduUpdatePort;

  User user;
  Goal goal;
  LocalDate nextMonday;
  LocalDate nextSunday;
  RepeatDdudu repeatDdudu;
  List<Ddudu> repeatedDdudus;
  String nameToUpdate;
  DayOfWeek originalRepeatDayOfWeek;
  DayOfWeek repeatDayOfWeekToUpdate;
  UpdateRepeatDduduRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    nextMonday = LocalDate.now()
        .with(DayOfWeek.MONDAY)
        .plusDays(7);
    nextSunday = nextMonday.plusDays(6);
    originalRepeatDayOfWeek = DayOfWeek.MONDAY;
    repeatDdudu = saveRepeatDduduPort.save(
        RepeatDdudu.builder()
            .name("반복 뚜두")
            .repeatType(RepeatType.WEEKLY)
            .repeatPattern(RepeatType.WEEKLY.createRepeatPattern(
                List.of(originalRepeatDayOfWeek.name()),
                null,
                null
            ))
            .goalId(goal.getId())
            .startDate(nextMonday)
            .endDate(nextSunday)
            .build()
    );
    repeatedDdudus = saveDduduPort.saveAll(
        repeatDduduDomainService.createRepeatedDdudus(user.getId(), repeatDdudu)
    );
    repeatDayOfWeekToUpdate = DayOfWeek.TUESDAY;
    nameToUpdate = "수정된 반복 뚜두";
    request = new UpdateRepeatDduduRequest(
        nameToUpdate,
        RepeatType.WEEKLY.name(),
        List.of(repeatDayOfWeekToUpdate.name()),
        null,
        null,
        nextMonday,
        nextSunday,
        null,
        null
    );
  }

  @Test
  void 반복_뚜두를_업데이트_하면_연결된_뚜두들도_함께_업데이트된다() {
    // when
    updateRepeatDduduService.update(user.getId(), repeatDdudu.getId(), request);

    // then
    RepeatDdudu updated = repeatDduduLoaderPort.getOptionalRepeatDdudu(repeatDdudu.getId())
        .get();
    assertThat(updated.getName()).isEqualTo(nameToUpdate);

    List<Ddudu> updatedDdudus = dduduLoaderPort.getRepeatedDdudus(repeatDdudu);

    Assertions.assertThat(updatedDdudus)
        .hasSize(1);
    Assertions.assertThat(updatedDdudus)
        .extracting(Ddudu::getName)
        .containsExactly(nameToUpdate);
    assertThat(updatedDdudus.get(0)
        .getScheduledOn()
        .getDayOfWeek())
        .isEqualTo(repeatDayOfWeekToUpdate);
  }

  @Test
  void 이미_완료된_반복_뚜두는_변경되지_않는다() {
    // given
    dduduUpdatePort.update(repeatedDdudus.get(0)
        .switchStatus());

    // when
    updateRepeatDduduService.update(user.getId(), repeatDdudu.getId(), request);

    // then
    List<Ddudu> updatedDdudus = dduduLoaderPort.getRepeatedDdudus(repeatDdudu);

    Assertions.assertThat(updatedDdudus)
        .hasSize(2);
    Assertions.assertThat(updatedDdudus)
        .extracting(ddudu -> ddudu.getScheduledOn()
            .getDayOfWeek())
        .containsExactlyInAnyOrder(originalRepeatDayOfWeek, repeatDayOfWeekToUpdate);
  }

}
