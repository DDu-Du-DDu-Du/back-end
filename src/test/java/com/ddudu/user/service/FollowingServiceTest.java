package com.ddudu.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

import com.ddudu.common.exception.BadRequestException;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.user.domain.Following;
import com.ddudu.user.domain.FollowingStatus;
import com.ddudu.user.domain.Options;
import com.ddudu.user.domain.User;
import com.ddudu.user.dto.request.FollowRequest;
import com.ddudu.user.dto.request.UpdateFollowingRequest;
import com.ddudu.user.dto.response.FollowingResponse;
import com.ddudu.user.exception.FollowingErrorCode;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.repository.FollowingDao;
import com.ddudu.user.repository.UserDao;
import java.util.Objects;
import java.util.Optional;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class FollowingServiceTest {

  static final Faker faker = new Faker();

  @Autowired
  UserDao userRepository;

  @Autowired
  FollowingService followingService;

  @Autowired
  FollowingDao followingRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  private User createUser() {
    String email = faker.internet()
        .emailAddress();
    String password = faker.internet()
        .password(8, 40, true, true, true);
    String nickname = faker.oscarMovie()
        .character();

    User user = User.builder()
        .passwordEncoder(passwordEncoder)
        .email(email)
        .password(password)
        .nickname(nickname)
        .build();

    return userRepository.save(user);
  }

  private Following createFollowing(User follower, User followee, FollowingStatus status) {
    FollowingStatus followingStatus = Objects.nonNull(status) ? status : FollowingStatus.FOLLOWING;
    Following following = Following.builder()
        .followee(followee)
        .follower(follower)
        .status(followingStatus)
        .build();

    return followingRepository.save(following);
  }

  @Nested
  class 팔로잉_생성_테스트 {

    @Test
    void 팔로잉_생성을_성공한다() {
      // given
      User follower = createUser();
      User followee = createUser();
      FollowRequest request = new FollowRequest(followee.getId());

      // when
      FollowingResponse expected = followingService.create(follower.getId(), request);

      // then
      Optional<Following> found = followingRepository.findById(expected.id());

      assertThat(found).isPresent();

      Following actual = found.get();

      assertThat(actual).extracting("follower", "followee", "status")
          .containsExactly(follower, followee, FollowingStatus.FOLLOWING);
    }

    @Test
    void 팔로잉_대상이_허락_옵션을_켜놨으면_팔로우_신청이_요청으로_생성된다() {
      //given
      User follower = createUser();
      User followee = createUser();
      Options followeeOptions = followee.getOptions();

      followeeOptions.switchOptions();

      FollowRequest request = new FollowRequest(followee.getId());

      // when
      FollowingResponse response = followingService.create(follower.getId(), request);

      // then
      assertThat(response.status()).isEqualTo(FollowingStatus.REQUESTED);
    }

    @Test
    void 사용자가_존재하지_않으면_팔로잉_생성을_실패한다() {
      // given
      long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);
      User followee = createUser();
      FollowRequest request = new FollowRequest(followee.getId());

      // when
      ThrowingCallable create = () -> followingService.create(invalidId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(FollowingErrorCode.FOLLOWER_NOT_EXISTING.getMessage());
    }

    @Test
    void 팔로우_대상이_존재하지_않으면_팔로잉_생성을_실패한다() {
      // given
      User follower = createUser();
      long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);
      FollowRequest request = new FollowRequest(invalidId);

      // when
      ThrowingCallable create = () -> followingService.create(follower.getId(), request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(FollowingErrorCode.FOLLOWEE_NOT_EXISTING.getMessage());
    }

    @Test
    void 이미_팔로우를_했으면_팔로잉_생성을_실패한다() {
      // given
      User follower = createUser();
      User followee = createUser();

      Following following = Following.builder()
          .followee(followee)
          .follower(follower)
          .build();

      followingRepository.save(following);

      FollowRequest request = new FollowRequest(followee.getId());

      // when
      ThrowingCallable construct = () -> followingService.create(follower.getId(), request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(construct)
          .withMessage(FollowingErrorCode.ALREADY_FOLLOWING.getMessage());
    }

  }

  @Nested
  class 팔로잉_수정_테스트 {

    @Test
    void 팔로잉_요청을_수락한다() {
      // given
      User follower = createUser();
      User followee = createUser();
      Following following = createFollowing(follower, followee, FollowingStatus.REQUESTED);
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      // when
      FollowingResponse response = followingService.updateStatus(
          followee.getId(), following.getId(), request);

      // then
      assertThat(response).extracting("id", "followerId", "followeeId", "status")
          .containsExactly(
              following.getId(), follower.getId(), followee.getId(), FollowingStatus.FOLLOWING);
    }

    @Test
    void 팔로잉_요청을_무시한다() {
      // given
      User follower = createUser();
      User followee = createUser();
      Following following = createFollowing(follower, followee, FollowingStatus.REQUESTED);
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.IGNORED);

      // when
      FollowingResponse response = followingService.updateStatus(
          followee.getId(), following.getId(), request);

      // then
      assertThat(response).extracting("id", "followerId", "followeeId", "status")
          .containsExactly(
              following.getId(), follower.getId(), followee.getId(), FollowingStatus.IGNORED);
    }

    @Test
    void 존재하지_않는_사용자면_실패한다() {
      // given
      long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      // when
      ThrowingCallable updateStatus = () -> followingService.updateStatus(
          invalidId, invalidId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(updateStatus)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 존재하지_않는_아이디면_실패한다() {
      // given
      User follower = createUser();
      long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      // when
      ThrowingCallable updateStatus = () -> followingService.updateStatus(
          follower.getId(), invalidId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(updateStatus)
          .withMessage(FollowingErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 팔로잉을_요청_상태로_수정_시도_시_실패한다() {
      // given
      User follower = createUser();
      User followee = createUser();
      Following following = createFollowing(follower, followee, FollowingStatus.FOLLOWING);
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.REQUESTED);

      // when
      ThrowingCallable updateStatus = () -> followingService.updateStatus(
          followee.getId(), following.getId(), request);

      // then
      assertThatExceptionOfType(BadRequestException.class).isThrownBy(updateStatus)
          .withMessage(FollowingErrorCode.REQUEST_UNAVAILABLE.getMessage());
    }

    @Test
    void 팔로잉_요청_받은_사용자가_아니면_수정을_실패한다() {
      // given
      User follower = createUser();
      User followee = createUser();
      Following following = createFollowing(follower, followee, FollowingStatus.REQUESTED);
      User invalidUser = createUser();
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      // when
      ThrowingCallable updateStatus = () -> followingService.updateStatus(
          invalidUser.getId(), following.getId(), request);

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(updateStatus)
          .withMessage(FollowingErrorCode.NOT_ENGAGED_USER.getMessage());
    }

  }

  @Nested
  class 팔로잉_삭제_테스트 {

    @Test
    void 존재하는_팔로잉_삭제를_성공한다() {
      // given
      User follower = createUser();
      User followee = createUser();
      Following following = createFollowing(follower, followee, null);

      // when
      followingService.delete(follower.getId(), following.getId());

      //then
      Optional<Following> actual = followingRepository.findById(following.getId());

      assertThat(actual).isEmpty();
    }

    @Test
    void 팔로잉이_존재하지_않을_때_예외를_발생시키지_않는다() {
      // given
      User follower = createUser();
      long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable delete = () -> followingService.delete(follower.getId(), invalidId);

      // then
      assertThatNoException().isThrownBy(delete);
    }

  }

}
