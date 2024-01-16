package com.ddudu.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.auth.jwt.converter.JwtConverter;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.common.exception.InvalidTokenException;
import com.ddudu.user.domain.Options;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.ToggleOptionResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserServiceTest {

  static final Faker faker = new Faker();

  UserBuilder builderWithEncoder;
  String password;
  String nickname;

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  JwtEncoder jwtEncoder;

  @Autowired
  JwtConverter jwtConverter;

  @BeforeEach
  void setUp() {
    builderWithEncoder = User.builder()
        .passwordEncoder(passwordEncoder);
    password = faker.internet()
        .password(8, 40, true, true, true);
    nickname = faker.oscarMovie()
        .character();
  }

  @Nested
  class 회원가입_테스트 {

    static Stream<Arguments> provideSignUpRequestAndString() {
      String username = faker.internet()
          .username();
      String email = faker.internet()
          .emailAddress();
      String intro = faker.howIMetYourMother()
          .catchPhrase();
      String password = faker.internet()
          .password(8, 40, true, true, true);
      String nickname = faker.oscarMovie()
          .character();

      return Stream.of(
          Arguments.of(new SignUpRequest(username, email, password, nickname, intro), "전부 기입"),
          Arguments.of(new SignUpRequest(null, email, password, nickname, intro), "선택 아이디만 미입력"),
          Arguments.of(new SignUpRequest(username, email, password, nickname, null), "자기소개만 미입력"),
          Arguments.of(new SignUpRequest(null, email, password, nickname, null), "선택 아이디와 자기소개 미입력")
      );
    }

    @ParameterizedTest(name = "{1}하면 회원가입을 성공한다")
    @MethodSource("provideSignUpRequestAndString")
    void 회원가입을_성공한다(SignUpRequest request, String message) {
      // when
      SignUpResponse expected = userService.signUp(request);

      // then
      Optional<User> actual = userRepository.findById(expected.id());

      assertThat(actual).isNotEmpty();
      assertThat(actual.get()).extracting("id", "email", "nickname")
          .containsExactly(expected.id(), expected.email(), expected.nickname());
    }

    @Test
    void 이메일이_이미_존재하면_회원가입을_실패한다() {
      // given
      User user = createUser(null, null, null);
      SignUpRequest request = new SignUpRequest(null, user.getEmail(), password, nickname, null);

      // when
      ThrowingCallable signUp = () -> userService.signUp(request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(signUp)
          .withMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    void 선택_아이디가_이미_존재하면_회원가입을_실패한다() {
      // given
      String username = faker.internet()
          .username();

      createUser(null, username, null);

      String differentEmail = faker.internet()
          .emailAddress();
      SignUpRequest request = new SignUpRequest(username, differentEmail, password, nickname, null);

      // when
      ThrowingCallable signUp = () -> userService.signUp(request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(signUp)
          .withMessage(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME.getMessage());
    }

  }

  @Nested
  class 사용자_단일_조회_테스트 {

    @Test
    void 사용자_단일_조회를_성공한다() {
      // given
      User expected = createUser(null, null, null);

      // when
      UserResponse actual = userService.findById(expected.getId());

      // then
      assertThat(actual.id()).isEqualTo(expected.getId());
    }

    @Test
    void 존재하지_않는_사용자_아이디_단일_조회를_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable login = () -> userService.findById(randomId);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(login)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
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
  class 이메일_변경_테스트 {

    @Test
    void 이메일_변경에_성공한다() {
      // given
      User user = createUser(null, null, null);
      Long userId = user.getId();

      String newEmail = faker.internet()
          .emailAddress();
      UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest(newEmail);

      // when
      userService.updateEmail(userId, updateEmailRequest);

      // then
      User updatedUser = userRepository.findById(userId)
          .get();
      assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void 존재하지_않는_사용자_아이디_이메일_변경을_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String newEmail = faker.internet()
          .emailAddress();

      // when
      ThrowingCallable updateEmail = () -> userService.updateEmail(
          randomId, new UpdateEmailRequest(newEmail));

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(updateEmail)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 변경할_이메일이_기존_이메일과_동일한_경우_이메일_변경을_실패한다() {
      // given
      User user = createUser(null, null, null);
      UpdateEmailRequest request = new UpdateEmailRequest(user.getEmail());

      // when
      ThrowingCallable updateEmail = () -> userService.updateEmail(
          user.getId(), request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(updateEmail)
          .withMessage(UserErrorCode.DUPLICATE_EXISTING_EMAIL.getMessage());
    }

    @Test
    void 이미_존재하는_이메일로_변경하면_이메일_변경을_실패한다() {
      // given
      User user = createUser(null, null, null);
      String differentEmail = faker.internet()
          .emailAddress();

      createUser(differentEmail, null, null);

      UpdateEmailRequest request = new UpdateEmailRequest(differentEmail);

      // when
      ThrowingCallable updateEmail = () -> userService.updateEmail(
          user.getId(), request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(updateEmail)
          .withMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
    }

  }

  @Nested
  class 비밀번호_변경_테스트 {

    @Test
    void 비밀번호_변경을_성공한다() {
      // given
      String newPassword = faker.internet()
          .password(8, 40, true, true, true);

      User user = createUser(null, null, null);
      Long userId = user.getId();

      UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(newPassword);

      // when
      userService.updatePassword(userId, updatePasswordRequest);

      // then
      User updatedUser = userRepository.findById(userId)
          .get();
      assertThat(updatedUser.getPassword()
          .check(newPassword, passwordEncoder)).isTrue();
    }

    @Test
    void 존재하지_않는_사용자_아이디_비밀번호_변경을_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String newPassword = faker.internet()
          .password(8, 40, true, true, true);

      // when
      ThrowingCallable updatePassword = () -> userService.updatePassword(
          randomId, new UpdatePasswordRequest(newPassword));

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(updatePassword)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 변경할_비밀번호가_기존_비밀번호와_동일한_경우_비밀번호_변경을_실패한다() {
      // given
      User user = createUser(null, null, null);
      Long userId = user.getId();
      UpdatePasswordRequest request = new UpdatePasswordRequest(password);

      // when
      ThrowingCallable updatePassword = () -> userService.updatePassword(userId, request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(updatePassword)
          .withMessage(UserErrorCode.DUPLICATE_EXISTING_PASSWORD.getMessage());
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
      Options options = user.getOptions();

      options.switchOptions();

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

  private User createUser(String email, String optionalUsername, String introduction) {
    if (Objects.isNull(email)) {
      email = faker.internet()
          .emailAddress();
    }

    User user = builderWithEncoder
        .email(email)
        .password(password)
        .nickname(nickname)
        .optionalUsername(Objects.nonNull(optionalUsername) ? optionalUsername : null)
        .introduction(Objects.nonNull(introduction) ? introduction : null)
        .build();

    return userRepository.save(user);
  }

}
