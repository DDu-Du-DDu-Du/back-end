package com.ddudu.domain.planning.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.goal.dto.CreateGoalCommand;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.GoalFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalDomainServiceTest {

  static GoalDomainService goalDomainService;

  @BeforeAll
  static void setUp() {
    goalDomainService = new GoalDomainService();
  }

  @Nested
  class 목표_생성_테스트 {

    Long userId;
    String name;
    String privacyType;
    String color;
    CreateGoalCommand request;

    @BeforeEach
    void setUp() {
      userId = GoalFixture.getRandomId();
      name = GoalFixture.getRandomSentenceWithMax(50);
      privacyType = GoalFixture.getRandomPrivacyType()
          .name();
      color = BaseFixture.getRandomColor();
      request = new CreateGoalCommand(name, color, privacyType);
    }

    @Test
    void 목표를_생성한다() {
      // when
      Goal actual = goalDomainService.create(userId, request);

      // then
      assertThat(actual).extracting("userId", "name", "privacyType", "color")
          .containsExactly(userId, name, PrivacyType.from(privacyType), color);
    }

  }

  @Nested
  class 기본_목표_생성_테스트 {

    Long userId;

    @BeforeEach
    void setUp() {
      userId = GoalFixture.getRandomId();
    }

    @Test
    void 목표를_생성한다() {
      // when
      List<Goal> actual = goalDomainService.createDefaultGoals(userId);

      // then
      assertEquals(3, actual.size());
      for (int i = 0; i < actual.size(); i++) {
        Goal goal = actual.get(i);
        assertEquals(userId, goal.getUserId());
        assertEquals("목표 " + (i + 1), goal.getName());
        assertEquals(PrivacyType.PUBLIC, goal.getPrivacyType());
      }
    }

  }

}
