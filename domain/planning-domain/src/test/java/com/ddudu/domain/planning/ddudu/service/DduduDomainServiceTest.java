package com.ddudu.domain.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.dto.CreateDduduCommand;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduDomainServiceTest {
  
  static DduduDomainService dduduDomainService;
  
  @BeforeAll
  static void setUp() {
    dduduDomainService = new DduduDomainService();
  }

  @Nested
  class 뚜두_생성_테스트 {

    Long userId;
    Long goalId;
    String name;
    LocalDate scheduledOn;
    CreateDduduCommand command;

    @BeforeEach
    void setUp() {
      userId = GoalFixture.getRandomId();
      goalId = GoalFixture.getRandomId();
      name = DduduFixture.getRandomSentenceWithMax(50);
      scheduledOn = LocalDate.now();
      command = new CreateDduduCommand(goalId, name, scheduledOn);
    }

    @Test
    void 뚜두를_생성한다() {
      // when
      Ddudu actual = dduduDomainService.create(userId, command);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "scheduledOn")
          .containsExactly(userId, goalId, name, scheduledOn);
    }

    @Test
    void 날짜가_설정되지_않으면_뚜두의_날짜가_생성_날짜가_된다() {
      // when
      CreateDduduCommand request = new CreateDduduCommand(goalId, name, null);
      Ddudu actual = dduduDomainService.create(userId, request);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "scheduledOn")
          .containsExactly(userId, goalId, name, LocalDate.now());
    }

  }

}
