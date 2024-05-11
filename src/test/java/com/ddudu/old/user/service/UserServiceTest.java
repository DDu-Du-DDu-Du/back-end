package com.ddudu.old.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.exception.UserErrorCode;
import com.ddudu.old.user.domain.Following;
import com.ddudu.old.user.domain.FollowingRepository;
import com.ddudu.old.user.domain.FollowingStatus;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import com.ddudu.old.user.dto.SimpleUserDto;
import com.ddudu.old.user.dto.request.UpdateProfileRequest;
import com.ddudu.old.user.dto.response.ToggleOptionResponse;
import com.ddudu.old.user.dto.response.UserProfileResponse;
import com.ddudu.old.user.dto.response.UsersResponse;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.jwt.converter.JwtConverter;
import java.util.List;
import java.util.Objects;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserServiceTest {

  static final Faker faker = new Faker();

  String nickname;

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  FollowingRepository followingRepository;

  @Autowired
  JwtEncoder jwtEncoder;

  @Autowired
  JwtConverter jwtConverter;

  @BeforeEach
  void setUp() {
    nickname = faker.oscarMovie()
        .character();
  }

  @Nested
  class 사용자_단일_조회_테스트 {

    @Test
    void 사용자_단일_조회를_성공한다() {
      // given
      User expected = createUser(null, null, null);

      // when
      UserProfileResponse actual = userService.findById(expected.getId());

      // then
      assertThat(actual.id()).isEqualTo(expected.getId());
    }

    @Test
    void 존재하지_않는_사용자_아이디_단일_조회를_실패한다() {
      // given
      long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable login = () -> userService.findById(invalidId);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(login)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 사용자_검색_테스트 {

    String email;
    String optionalUsername;

    @BeforeEach
    void setUp() {
      email = faker.internet()
          .emailAddress();
      optionalUsername = faker.internet()
          .username();
    }

    @Test
    void 아이디로_사용자_검색에_성공한다() {
      // when
      List<UserProfileResponse> searched = userService.search(
          optionalUsername, UserSearchType.OPTIONAL_USERNAME);

      // then
      assertThat(searched).hasSize(1);
      User actual = userRepository.findById(searched.get(0)
              .id())
          .get();
      assertThat(actual
          .getUsername()).isEqualTo(optionalUsername);
    }

    @Test
    void 닉네임으로_사용자_검색에_성공한다() {
      // when
      List<UserProfileResponse> searched = userService.search(nickname, UserSearchType.NICKNAME);

      // then
      assertThat(searched).hasSize(1);
      assertThat(searched.get(0)
          .nickname()).isEqualTo(nickname);
    }

    @ParameterizedTest(name = "{0}일 때, 빈 리스트를 응답한다.")
    @EmptySource
    @Disabled
    void 키워드가_공백이면_빈_리스트를_반환한다(String keyword) {
      // given

      // when
      List<UserProfileResponse> actual = userService.search(keyword, UserSearchType.EMAIL);

      // then
      assertThat(actual).isEmpty();
    }

    @Test
    void 검색_유형이_입력되지_않은_경우_키워드가_이메일_형식이_아니면_아이디로_검색한다() {
      // when
      List<UserProfileResponse> searched = userService.search(optionalUsername, null);

      // then
      assertThat(searched).hasSize(1);
      User actual = userRepository.findById(searched.get(0)
              .id())
          .get();
      assertThat(actual
          .getUsername()).isEqualTo(optionalUsername);
    }

  }

  @Nested
  class 프로필_수정 {

    String introduction;

    @BeforeEach
    void setUp() {
      introduction = faker.book()
          .title();
    }

    @Test
    void 사용자_프로필_수정을_성공한다() {
      // given
      User user = createUser(null, null, null);

      String newNickname = faker.oscarMovie()
          .character();
      String newIntroduction = faker.book()
          .title();
      UpdateProfileRequest request = new UpdateProfileRequest(newNickname, newIntroduction);

      // when
      userService.updateProfile(user.getId(), request);

      // then
      User actual = userRepository.findById(user.getId())
          .get();
      assertThat(actual).extracting("nickname", "introduction")
          .containsExactly(newNickname, newIntroduction);
    }

  }

  @Nested
  class 옵션_토글_서비스_테스트 {

    @Test
    void 수락한_사람만_팔로우_받기_옵션을_킨다() {
      // given
      User user = createUser(null, null, null);

      // when
      ToggleOptionResponse response = userService.switchOption(user.getId());

      // then
      assertThat(response.allowFollowsAfterApproval()).isTrue();
    }

    @Test
    void 수락한_사람만_팔로우_받기_옵션을_끈다() {
      // given
      User user = createUser(null, null, null);
      user.switchOptions();

      userRepository.update(user);

      // when
      ToggleOptionResponse response = userService.switchOption(user.getId());

      // then
      assertThat(response.allowFollowsAfterApproval()).isFalse();
    }

    @Test
    void 존재하지_않는_사용자면_옵션_변경을_실패한다() {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable toggleOption = () -> userService.switchOption(userId);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(toggleOption)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 팔로워_팔로이_조회_테스트 {

    @ParameterizedTest(name = "조회 대상: {0}")
    @EnumSource(FollowingSearchType.class)
    void 사용자의_팔로잉_정보_조회를_성공한다(FollowingSearchType searchType) {
      // given
      User user = createUser(null, null, null);
      User target = createUser(null, null, null);

      if (FollowingSearchType.isSearchingFollower(searchType)) {
        createFollowing(target, user, null);
      } else {
        createFollowing(user, target, null);
      }

      // when
      UsersResponse actual = userService.findFromFollowings(user.getId(), searchType);

      // then
      SimpleUserDto expected = SimpleUserDto.from(target);
      assertThat(actual.counts()).isEqualTo(1);
      assertThat(actual.users()).containsOnly(expected);
    }

    @ParameterizedTest(name = "조회 대상: {0}")
    @EnumSource(FollowingSearchType.class)
    void 요청됨이나_무시_상태가_아닌_팔로잉만_조회한다(FollowingSearchType searchType) {
      // given
      User user = createUser(null, null, null);
      User requestedFollowee = createUser(null, null, null);
      User rejectedFollowee = createUser(null, null, null);
      createFollowing(user, requestedFollowee, FollowingStatus.REQUESTED);
      createFollowing(user, rejectedFollowee, FollowingStatus.IGNORED);

      // when
      UsersResponse actual = userService.findFromFollowings(user.getId(), searchType);

      // then
      assertThat(actual.users()).isEmpty();
    }

    @ParameterizedTest(name = "조회 대상: {0}")
    @EnumSource(FollowingSearchType.class)
    void 사용자가_존재하지_않으면_조회를_실패한다(FollowingSearchType searchType) {
      // given
      long loginId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable findFollowees = () -> userService.findFromFollowings(loginId, searchType);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findFollowees)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

    private Following createFollowing(User follower, User followee, FollowingStatus status) {
      Following following = Following.builder()
          .follower(follower)
          .followee(followee)
          .status(Objects.nonNull(status) ? status : null)
          .build();

      return followingRepository.save(following);
    }

  }

  private User createUser(String email, String optionalUsername, String introduction) {
    User user = User.builder()
        .build();

    return userRepository.save(user);
  }

}
