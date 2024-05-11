package com.ddudu.old.like.service;

import static com.ddudu.old.todo.domain.TodoStatus.COMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.like.dto.request.LikeRequest;
import com.ddudu.old.like.dto.response.LikeResponse;
import com.ddudu.old.like.exception.LikeErrorCode;
import com.ddudu.old.todo.domain.Todo;
import com.ddudu.old.todo.domain.TodoRepository;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
import java.util.Optional;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
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
class LikeServiceTest {

  static final Faker faker = new Faker();

  @Autowired
  LikeService likeService;

  @Autowired
  LikeRepository likeRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  GoalRepository goalRepository;

  @Autowired
  TodoRepository todoRepository;

  User user;
  Goal goal;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = createUser();
    goal = createGoal(user);
    todo = createCompletedTodo(user, goal);
  }

  @Nested
  class 좋아요_토글_테스트 {

    @Test
    void 좋아요_취소를_성공한다() {
      // given
      User other = createUser();
      LikeRequest request = new LikeRequest(other.getId(), todo.getId());
      Like like = createLike(other, todo);

      // when
      likeService.toggle(other.getId(), request);

      // then
      Optional<Like> actual = likeRepository.findById(like.getId());
      assertThat(actual).isNotPresent();
    }

    @Test
    void 취소한_좋아요를_다시_좋아요할_수_있다() {
      // given
      User other = createUser();
      LikeRequest request = new LikeRequest(other.getId(), todo.getId());
      Like like = createLike(other, todo);
      likeRepository.delete(like);

      // when
      LikeResponse expected = likeService.toggle(other.getId(), request);

      // then
      Like actual = likeRepository.findByUserAndTodo(other, todo);
      assertThat(actual).isNotNull();
    }

    @Test
    void 좋아요한_이력이_없으면_좋아요를_생성한다() {
      User other = createUser();
      LikeRequest request = new LikeRequest(other.getId(), todo.getId());

      // when
      LikeResponse expected = likeService.toggle(other.getId(), request);

      // then
      Optional<Like> actual = likeRepository.findById(expected.id());
      assertThat(actual).isPresent();
      assertThat(expected).extracting("userId", "todoId")
          .containsExactly(other.getId(), todo.getId());
    }

    @Test
    void 로그인한_사용자가_좋아요할_사용자와_다르면_좋아요_토글을_실패한다() {
      // given
      User other = createUser();
      LikeRequest request = new LikeRequest(user.getId(), todo.getId());

      // when
      ThrowingCallable toggle = () -> likeService.toggle(other.getId(), request);

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(toggle)
          .withMessage(LikeErrorCode.INVALID_AUTHORITY.getMessage());
    }

    @Test
    void 사용자_아이디가_존재하지_않으면_좋아요_토글을_실패한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LikeRequest request = new LikeRequest(invalidUserId, todo.getId());

      // when
      ThrowingCallable toggle = () -> likeService.toggle(invalidUserId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(toggle)
          .withMessage(LikeErrorCode.USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 할_일_아이디가_존재하지_않으면_좋아요_토글을_실패한다() {
      // given
      User other = createUser();
      Long invalidTodoId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LikeRequest request = new LikeRequest(other.getId(), invalidTodoId);

      // when
      ThrowingCallable toggle = () -> likeService.toggle(other.getId(), request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(toggle)
          .withMessage(LikeErrorCode.TODO_NOT_EXISTING.getMessage());
    }

  }

  private User createUser() {
    String email = faker.internet()
        .emailAddress();
    String password = faker.internet()
        .password(8, 40, true, true, true);
    String nickname = faker.oscarMovie()
        .character();

    User user = User.builder()
        .build();

    return userRepository.save(user);
  }

  private Goal createGoal(User user) {
    String name = faker.lorem()
        .word();

    Goal goal = Goal.builder()
        .name(name)
        .user(user)
        .privacyType(PrivacyType.PUBLIC)
        .build();

    return goalRepository.save(goal);
  }

  private Todo createCompletedTodo(User user, Goal goal) {
    String name = faker.lorem()
        .word();

    Todo todo = Todo.builder()
        .name(name)
        .user(user)
        .goal(goal)
        .status(COMPLETE)
        .build();

    return todoRepository.save(todo);
  }

  private Like createLike(User user, Todo todo) {
    Like like = Like.builder()
        .user(user)
        .todo(todo)
        .build();

    return likeRepository.save(like);
  }

}
