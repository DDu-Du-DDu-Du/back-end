package com.ddudu.application.service.repeat_ddudu;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.domain.enums.RepeatType;
import com.ddudu.application.domain.repeat_ddudu.service.RepeatDduduDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.repeat_ddudu.RepeatPatternDto;
import com.ddudu.application.dto.repeat_ddudu.request.UpdateRepeatDduduRequest;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.DduduUpdatePort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.SaveRepeatDduduPort;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user));
    nextMonday = LocalDate.now()
        .with(DayOfWeek.MONDAY)
        .plusDays(7);
    nextSunday = nextMonday.plusDays(6);
    originalRepeatDayOfWeek = DayOfWeek.MONDAY;
    repeatDdudu = saveRepeatDduduPort.save(
        RepeatDdudu.builder()
            .name("반복 뚜두")
            .repeatType(RepeatType.WEEKLY)
            .repeatPatternDto(
                RepeatPatternDto.weeklyPatternOf(List.of(originalRepeatDayOfWeek.name())))
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
    assertThat(updatedDdudus).hasSize(1);
    assertThat(updatedDdudus).extracting(Ddudu::getName)
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
    assertThat(updatedDdudus).hasSize(2);
    assertThat(updatedDdudus)
        .extracting(ddudu -> ddudu.getScheduledOn()
            .getDayOfWeek())
        .containsExactlyInAnyOrder(originalRepeatDayOfWeek, repeatDayOfWeekToUpdate);
  }

}
