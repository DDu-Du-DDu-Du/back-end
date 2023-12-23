package com.ddudu.goal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.repository.GoalRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

    private String validName;
    private String validColor;

    목표_생성_테스트() {
      validName = "dev course";
      validColor = "F7A29D";
    }

    @Test
    void 목표를_생성할_수_있다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()).extracting("name", "color")
          .containsExactly(validName, validColor);
    }

    @Test
    void 목표_생성_시_ID가_자동_생성된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getId()).isNotNull();
    }

    @Test
    void 목표_생성_시_IN_PROGRESS_상태가_된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
    }

    @ParameterizedTest(name = "유효하지 않은 색상 : {0}")
    @NullAndEmptySource
    void 색상을_설정하지_않거나_빈_문자열이면_기본값이_적용된다(String invalidColor) {
      // given
      String defaultColor = "191919";

      CreateGoalRequest request = new CreateGoalRequest(
          validName, invalidColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getColor()).isEqualTo(defaultColor);
    }

    @Test
    void 보기_설정을_설정하지_않으면_PRIVATE이_적용된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, null);

      // when
      CreateGoalResponse expected = goalService.create(request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getPrivacyType()).isEqualTo(PrivacyType.PRIVATE);
    }

  }

}
