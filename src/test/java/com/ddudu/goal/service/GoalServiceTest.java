package com.ddudu.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.repository.GoalRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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

  @Nested
  class 목표_조회_테스트 {

    private String validName;

    목표_조회_테스트() {
      validName = "dev course";
    }

    @Test
    void ID를_통해_단일_목표를_조회_할_수_있다() {
      // given
      Goal expected = createGoal(validName);
      Long id = expected.getId();

      // when
      GoalResponse actual = goalService.getGoal(id);

      // then
      GoalStatus expectedStatus = expected.getStatus();
      PrivacyType expectedPrivacyType = expected.getPrivacyType();

      assertThat(actual).extracting(
              "id",
              "name",
              "status",
              "color",
              "privacyType"
          )
          .containsExactly(
              id,
              expected.getName(),
              expectedStatus.name(),
              expected.getColor(),
              expectedPrivacyType.name()
          );
    }

    @Test
    void 유효하지_않은_ID인_경우_조회에_실패한다() {
      // given
      Long invalidId = -1L;

      // when
      ThrowingCallable getGoal = () -> goalService.getGoal(invalidId);

      // then
      assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(getGoal)
          .withMessage("해당 아이디를 가진 목표가 존재하지 않습니다.");
    }

    private Goal createGoal(String name) {
      Goal goal = Goal.builder()
          .name(name)
          .build();

      return goalRepository.save(goal);
    }

  }

}
