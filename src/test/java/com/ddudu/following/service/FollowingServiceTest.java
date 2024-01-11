package com.ddudu.following.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.following.domain.Following;
import com.ddudu.following.domain.FollowingStatus;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.dto.response.FollowResponse;
import com.ddudu.following.exception.FollowingErrorCode;
import com.ddudu.following.repository.FollowingRepository;
import com.ddudu.user.domain.Options;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
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
  UserRepository userRepository;

  @Autowired
  FollowingService followingService;

  @Autowired
  FollowingRepository followingRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Nested
  class 팔로잉_생성_테스트 {

    @Test
    void 사용자가_존재하지_않으면_팔로잉_생성을_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong();
      User followee = createUser();
      FollowRequest request = new FollowRequest(followee.getId());

      // when
      ThrowingCallable create = () -> followingService.create(randomId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(FollowingErrorCode.FOLLOWER_NOT_EXISTING.getMessage());
    }

    @Test
    void 팔로우_대상이_존재하지_않으면_팔로잉_생성을_실패한다() {
      // given
      User follower = createUser();
      long randomId = faker.random()
          .nextLong();
      FollowRequest request = new FollowRequest(randomId);

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

    @Test
    void 팔로잉_생성을_성공한다() {
      // given
      User follower = createUser();
      User followee = createUser();
      FollowRequest request = new FollowRequest(followee.getId());

      // when
      FollowResponse expected = followingService.create(follower.getId(), request);

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

      followeeOptions.toggleOptions();

      FollowRequest request = new FollowRequest(followee.getId());

      // when
      FollowResponse response = followingService.create(follower.getId(), request);

      // then
      assertThat(response.status()).isEqualTo(FollowingStatus.REQUESTED);
    }

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

  }

}
