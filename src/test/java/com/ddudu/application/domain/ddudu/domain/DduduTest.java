package com.ddudu.application.domain.ddudu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.ddudu.application.domain.ddudu.domain.Ddudu.DduduBuilder;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduTest {

  Goal goal;
  User user;

  @BeforeEach
  void setUp() {
    user = UserFixture.createRandomUserWithId();
    goal = GoalFixture.createRandomGoalWithUser(user);
  }

  @Nested
  class 생성_테스트 {

    String name;

    @BeforeEach
    void setUp() {
      name = DduduFixture.getRandomSentenceWithMax(50);
    }

    @Test
    void 뚜두_생성을_성공한다() {
      // given

      // when
      Ddudu ddudu = Ddudu.builder()
          .goal(goal)
          .user(user)
          .name(name)
          .status(DduduStatus.COMPLETE)
          .isPostponed(true)
          .build();

      // then
      assertThat(ddudu).isNotNull();
    }

    @Test
    void 뚜두_생성_시_디폴트_값이_적용된다() {
      // given

      // when
      Ddudu ddudu = Ddudu.builder()
          .goal(goal)
          .user(user)
          .name(name)
          .build();

      // then
      long timeDifference = LocalDateTime.now()
          .toEpochSecond(ZoneOffset.UTC) - ddudu.getBeginAt()
          .toEpochSecond(ZoneOffset.UTC);

      assertThat(ddudu.getStatus()).isEqualTo(DduduStatus.UNCOMPLETED);
      assertThat(ddudu.isPostponed()).isFalse();
      assertThat(timeDifference).isLessThanOrEqualTo(1);
    }

    @Test
    void 목표가_없으면_생성을_실패한다() {
      // given
      DduduBuilder builder = Ddudu.builder()
          .user(user)
          .name(name);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(DduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    }

    @Test
    void 사용자가_없으면_생성을_실패한다() {
      // given
      DduduBuilder builder = Ddudu.builder()
          .goal(goal)
          .name(name);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(DduduErrorCode.NULL_USER.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 이름이_빈_값이면_생성을_실패한다(String blankName) {
      // given
      DduduBuilder builder = Ddudu.builder()
          .goal(goal)
          .user(user)
          .name(blankName);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(DduduErrorCode.BLANK_NAME.getCodeName());
    }

    @Test
    void 이름이_50자를_넘으면_생성을_실패한다() {
      // given
      String over50 = DduduFixture.getRandomSentence(51, 100);
      DduduBuilder builder = Ddudu.builder()
          .goal(goal)
          .user(user)
          .name(over50);

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
    }

    @Test
    void 시작_시간이_종료_시간보다_뒤면_생성을_실패한다() {
      // given
      DduduBuilder builder = Ddudu.builder()
          .goal(goal)
          .user(user)
          .name(name)
          .beginAt(LocalDateTime.now()
              .plusMinutes(1))
          .endAt(LocalDateTime.now());

      // when
      ThrowingCallable create = builder::build;

      // then
      assertThatIllegalArgumentException().isThrownBy(create)
          .withMessage(DduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
    }

  }

  @Nested
  class 권한_테스트 {

    Ddudu ddudu;

    @BeforeEach
    void setUp() {
      ddudu = DduduFixture.createRandomDduduWithGoal(goal);
    }

    @Test
    void 권한_확인을_성공한다() {
      // given

      // when
      ThrowingCallable check = () -> ddudu.checkAuthority(user.getId());

      // then
      assertThatNoException().isThrownBy(check);
    }

    @Test
    void 사용자의_아이디가_다르면_권한_확인을_실패한다() {
      // given
      long wrongUserId = DduduFixture.getRandomId();

      // when
      ThrowingCallable check = () -> ddudu.checkAuthority(wrongUserId);

      // then
      assertThatExceptionOfType(SecurityException.class).isThrownBy(check)
          .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
    }

  }

  @Nested
  class 기간_설정_테스트 {

    Ddudu ddudu;

    @BeforeEach
    void setUp() {
      ddudu = DduduFixture.createRandomDduduWithGoal(goal);
    }

    @Test
    void 기간_설정을_성공한다() {
      // given
      LocalDateTime now = LocalDateTime.now();

      // when
      Ddudu actual = ddudu.setUpPeriod(now, now.plusDays(1));

      // then
      assertThat(actual)
          .hasFieldOrPropertyWithValue("id", ddudu.getId())
          .hasFieldOrPropertyWithValue("user", ddudu.getUser())
          .hasFieldOrPropertyWithValue("name", ddudu.getName())
          .hasFieldOrPropertyWithValue("isPostponed", ddudu.isPostponed())
          .hasFieldOrPropertyWithValue("status", ddudu.getStatus())
          .hasFieldOrPropertyWithValue("goal", ddudu.getGoal())
          .hasFieldOrPropertyWithValue("beginAt", now)
          .hasFieldOrPropertyWithValue("endAt", now.plusDays(1));
    }

    @Test
    void 시작_시간만_설정할_수_있다() {
      // given
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expectedEndTime = ddudu.getEndAt();

      // when
      Ddudu actual = ddudu.setUpPeriod(now, null);

      // then
      assertThat(actual.getBeginAt()).isEqualTo(now);
      assertThat(actual.getEndAt()).isEqualTo(expectedEndTime);
    }

    @Test
    void 종료_시간만_설정할_수_있다() {
      // given
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expectedBeginAt = ddudu.getBeginAt();

      // when
      Ddudu actual = ddudu.setUpPeriod(null, now);

      // then
      assertThat(actual.getBeginAt()).isEqualTo(expectedBeginAt);
      assertThat(actual.getEndAt()).isEqualTo(now);
    }

  }

}