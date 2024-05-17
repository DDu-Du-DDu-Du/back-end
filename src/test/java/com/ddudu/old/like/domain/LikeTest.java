package com.ddudu.old.like.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.like.exception.LikeErrorCode;
import com.ddudu.presentation.api.exception.InvalidParameterException;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class LikeTest {

  static final Faker faker = new Faker();

  User user;
  Goal goal;
  Ddudu ddudu;

  @BeforeEach
  void setUp() {
    user = createUser();
    goal = createGoal(user);
    ddudu = createTodo(user, goal);
  }

  @Nested
  class 좋아요_생성_테스트 {

    @Test
    void 사용자가_NULL이면_좋아요_생성을_실패한다() {
      // when
      ThrowingCallable construct = () -> Like.builder()
          .ddudu(ddudu)
          .build();

      //then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(LikeErrorCode.NULL_USER.getMessage());
    }

    @Test
    void 좋아요할_할_일이_NULL이면_좋아요_생성을_실패한다() {
      // when
      ThrowingCallable construct = () -> Like.builder()
          .user(user)
          .build();

      //then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(LikeErrorCode.NULL_TODO.getMessage());
    }

    @Test
    void 사용자는_본인의_할_일에_좋아요를_할_수_없다() {
      // when
      ThrowingCallable construct = () -> Like.builder()
          .user(user)
          .ddudu(ddudu)
          .build();

      //then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(LikeErrorCode.SELF_LIKE_UNAVAILABLE.getMessage());
    }

    @Test
    void 사용자는_미완료된_할_일에_좋아요를_할_수_없다() {
      // given
      User other = createUser();

      // when
      ThrowingCallable construct = () -> Like.builder()
          .user(other)
          .ddudu(ddudu)
          .build();

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(LikeErrorCode.UNAVAILABLE_UNCOMPLETED_TODO.getMessage());
    }

    @Test
    void 좋아요_생성을_성공한다() {
      // given
      User other = createUser();
      ddudu.switchStatus();

      // when
      Like like = Like.builder()
          .user(other)
          .ddudu(ddudu)
          .build();

      // then
      assertThat(like)
          .extracting("user", "todo")
          .containsExactly(other, ddudu);
    }

  }

  private User createUser() {
    String email = faker.internet()
        .emailAddress();
    String password = faker.internet()
        .password(8, 40, true, true, true);
    String nickname = faker.oscarMovie()
        .character();

    return User.builder()
        .build();
  }

  private Goal createGoal(User user) {
    String name = faker.lorem()
        .word();

    return Goal.builder()
        .name(name)
        .user(user)
        .build();
  }

  private Ddudu createTodo(User user, Goal goal) {
    String name = faker.lorem()
        .word();

    return Ddudu.builder()
        .name(name)
        .user(user)
        .goal(goal)
        .build();
  }

}
