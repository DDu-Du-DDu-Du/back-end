package com.ddudu.goal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.repository.GoalRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalServiceTest {

  @Autowired
  GoalService goalService;

  @Autowired
  GoalRepository goalRepository;

  @Nested
  class 목표_생성_테스트 {

    @Test
    void 목표를_생성할_수_있다() {
      // given
      String name = "dev course";
      String color = "191919";
      PrivacyType privacyType = PrivacyType.PUBLIC;

      CreateGoalRequest request = new CreateGoalRequest(name, color, privacyType);

      // when
      CreateGoalResponse expected = goalService.create(request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()).extracting("name", "color")
          .containsExactly(name, color);
    }

  }

}
