package com.ddudu.application.domain.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.util.ArrayList;
import java.util.List;
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
      request = new CreateGoalRequest(name, color, privacyType, new ArrayList<>());
    }

    @Test
    void 목표를_생성한다() {
      // when
      Goal actual = goalDomainService.create(user, request);

      // then
      assertThat(actual).extracting("userId", "name", "privacyType", "color")
          .containsExactly(user.getId(), name, PrivacyType.from(privacyType), color);
    }

  }

  @Nested
  class 기본_목표_생성_테스트 {

    User user;

    @BeforeEach
    void setUp() {
      user = UserFixture.createRandomUserWithId();
    }

    @Test
    void 목표를_생성한다() {
      // when
      List<Goal> actual = goalDomainService.createDefaultGoals(user);

      // then
      assertEquals(3, actual.size());
      for (int i = 0; i < actual.size(); i++) {
        Goal goal = actual.get(i);
        assertEquals(user.getId(), goal.getUserId());
        assertEquals("목표 " + (i + 1), goal.getName());
        assertEquals(PrivacyType.PUBLIC, goal.getPrivacyType());
      }
    }

  }

}
