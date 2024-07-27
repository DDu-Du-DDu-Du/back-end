package com.ddudu.application.domain.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(DduduDomainService.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduDomainServiceTest {

  @Autowired
  DduduDomainService dduduDomainService;

  @Nested
  class 뚜두_생성_테스트 {

    User user;
    Long goalId;
    String name;
    LocalDate scheduledOn;
    CreateDduduRequest request;

    @BeforeEach
    void setUp() {
      user = UserFixture.createRandomUserWithId();
      goalId = GoalFixture.getRandomId();
      name = DduduFixture.getRandomSentenceWithMax(50);
      scheduledOn = LocalDate.now();
      request = new CreateDduduRequest(goalId, name, scheduledOn);
    }

    @Test
    void 뚜두를_생성한다() {
      // when
      Ddudu actual = dduduDomainService.create(user, request);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "scheduledOn")
          .containsExactly(user.getId(), goalId, name, scheduledOn);
    }

    @Test
    void 날짜가_설정되지_않으면_뚜두의_날짜가_생성_날짜가_된다() {
      // when
      CreateDduduRequest request = new CreateDduduRequest(goalId, name, null);
      Ddudu actual = dduduDomainService.create(user, request);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "scheduledOn")
          .containsExactly(user.getId(), goalId, name, LocalDate.now());
    }

  }

}
