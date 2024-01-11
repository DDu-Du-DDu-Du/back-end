package com.ddudu.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.like.domain.Like;
import com.ddudu.like.dto.request.LikeRequest;
import com.ddudu.like.dto.response.LikeResponse;
import com.ddudu.like.exception.LikeErrorCode;
import com.ddudu.like.repository.LikeRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.repository.TodoRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

  @Autowired
  EntityManager entityManager;

  User user;
  Goal goal;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = createUser();
    goal = createGoal(user);
    todo = createTodo(user, goal);
    todo.switchStatus();
  }

  @Nested
  class 좋아요_생성_테스트 {

    @Test
    void 로그인한_사용자가_좋아요할_사용자와_다르면_좋아요_생성을_실패한다() {
      // given
      User other = createUser();
      LikeRequest request = new LikeRequest(user.getId(), todo.getId());

      // when
      ThrowingCallable create = () -> likeService.create(other.getId(), request);

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(create)
          .withMessage(LikeErrorCode.INVALID_AUTHORITY.getMessage());
    }

    @Test
    void 사용자_아이디가_존재하지_않으면_좋아요_생성을_실패한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LikeRequest request = new LikeRequest(invalidUserId, todo.getId());

      // when
      ThrowingCallable create = () -> likeService.create(invalidUserId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(LikeErrorCode.USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 할_일_아이디가_존재하지_않으면_좋아요_생성을_실패한다() {
      // given
      User other = createUser();
      Long invalidTodoId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LikeRequest request = new LikeRequest(other.getId(), invalidTodoId);

      // when
      ThrowingCallable create = () -> likeService.create(other.getId(), request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(LikeErrorCode.TODO_NOT_EXISTING.getMessage());
    }

    @Test
    void 이미_좋아요를_눌렀다면_좋아요_생성을_실패한다() {
      // given
      User other = createUser();

      Like like = Like.builder()
          .user(other)
          .todo(todo)
          .build();
      likeRepository.save(like);

      LikeRequest request = new LikeRequest(other.getId(), todo.getId());

      // when
      ThrowingCallable create = () -> likeService.create(other.getId(), request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(create)
          .withMessage(LikeErrorCode.ALREADY_LIKED.getMessage());
    }

    @Test
    void 완료된_할_일에_대해_좋아요_생성을_성공한다() {
      // given
      User other = createUser();
      LikeRequest request = new LikeRequest(other.getId(), todo.getId());

      // when
      LikeResponse expected = likeService.create(other.getId(), request);

      // then
      Optional<Like> actual = likeRepository.findById(expected.id());
      assertThat(actual).isPresent();
      assertThat(actual.get()).extracting("user", "todo")
          .containsExactly(other, todo);
    }

  }

  @Nested
  class 좋아요_취소_테스트 {

    @Test
    void 로그인한_사용자가_좋아요_취소할_사용자와_다르면_좋아요_취소를_실패한다() {
      // given
      Long loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      User other = createUser();
      Like like = createLike(other, todo);

      // when
      ThrowingCallable delete = () -> likeService.delete(loginId, like.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(delete)
          .withMessage(LikeErrorCode.INVALID_AUTHORITY.getMessage());
    }

    @Test
    void 좋아요가_존재하지_않을_때_예외를_발생시키지_않는다() {
      // given
      User other = createUser();
      Long invalidLikeId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable delete = () -> likeService.delete(other.getId(), invalidLikeId);

      // then
      assertThatNoException().isThrownBy(delete);
    }

    @Test
    void 이미_취소된_좋아요라면_예외를_발생새키지_않는다() {
      // given
      User other = createUser();
      Like like = createLike(other, todo);

      likeService.delete(other.getId(), like.getId());
      flushAndClearPersistence();

      // when
      ThrowingCallable delete = () -> likeService.delete(other.getId(), like.getId());

      // then
      assertThatNoException().isThrownBy(delete);
    }

    @Test
    void 좋아요_취소를_성공한다() {
      // given
      User other = createUser();
      Like like = createLike(other, todo);

      // when
      likeService.delete(other.getId(), like.getId());
      flushAndClearPersistence();

      // then
      Optional<Like> actual = likeRepository.findById(like.getId());
      assertThat(actual).isEmpty();
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
        .passwordEncoder(new BCryptPasswordEncoder())
        .email(email)
        .password(password)
        .nickname(nickname)
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

  private Todo createTodo(User user, Goal goal) {
    String name = faker.lorem()
        .word();

    Todo todo = Todo.builder()
        .name(name)
        .user(user)
        .goal(goal)
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

  private void flushAndClearPersistence() {
    entityManager.flush();
    entityManager.clear();
  }

}