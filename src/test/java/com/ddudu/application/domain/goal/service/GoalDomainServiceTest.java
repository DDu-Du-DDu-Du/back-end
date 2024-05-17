package com.ddudu.application.domain.goal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(GoalDomainService.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalDomainServiceTest {

  @Autowired
  GoalDomainService goalDomainService;

  @Nested
  class 목표_생성_테스트 {

    User user;
    String name;
    String privacyType;
    String color;
    CreateGoalRequest request;

    @BeforeEach
    void setUp() {
      user = UserFixture.createRandomUserWithId();
      name = BaseFixture.getRandomSentenceWithMax(50);
      privacyType = GoalFixture.getRandomPrivacyType()
          .name();
      color = BaseFixture.getRandomColor();
      request = new CreateGoalRequest(name, color, privacyType);
    }

    @Test
    void 목표를_생성한다() {
      // when
      Goal actual = goalDomainService.create(user, request);

      // then
      assertThat(actual).extracting("user", "name", "privacyType", "color")
          .containsExactly(user, name, PrivacyType.from(privacyType), color);
    }

  }

}
