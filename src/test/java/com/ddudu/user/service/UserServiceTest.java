package com.ddudu.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.auth.jwt.converter.JwtConverter;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.InvalidTokenException;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.repository.UserRepository;
import java.util.List;
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

    @Test
    void 이메일이_이미_존재하면_회원가입을_실패한다() {
      // given
      String email = faker.internet()
          .emailAddress();
      User user = builderWithEncoder
          .email(email)
          .password(password)
          .nickname(nickname)
          .build();

      userRepository.save(user);

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
      String email = faker.internet()
          .emailAddress();
      String username = faker.internet()
          .username();
      User user = builderWithEncoder
          .email(email)
          .password(password)
          .nickname(nickname)
          .optionalUsername(username)
          .build();

      userRepository.save(user);

      String differentEmail = faker.internet()
          .emailAddress();
      SignUpRequest request = new SignUpRequest(username, differentEmail, password, nickname, null);

      // when
      ThrowingCallable signUp = () -> userService.signUp(request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(signUp)
          .withMessage(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME.getMessage());
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

  }

  @Nested
  class 이메일_변경_테스트 {

    @Test
    void 로그인한_사용자와_이메일_변경할_사용자가_다르면_변경을_실패한다() {
      // given
      long loginRandomId = faker.random()
          .nextLong();
      long randomId = faker.random()
          .nextLong();
      String newEmail = faker.internet()
          .emailAddress();

      // when
      ThrowingCallable updateEmail = () -> userService.updateEmail(
          loginRandomId, randomId, new UpdateEmailRequest(newEmail));

      // then
      assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(updateEmail)
          .withMessage(UserErrorCode.INVALID_AUTHENTICATION.getMessage());
    }

    @Test
    void 존재하지_않는_사용자_아이디_이메일_변경을_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong();
      String newEmail = faker.internet()
          .emailAddress();

      // when
      ThrowingCallable updateEmail = () -> userService.updateEmail(
          randomId, randomId, new UpdateEmailRequest(newEmail));

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(updateEmail)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 변경할_이메일이_기존_이메일과_동일한_경우_이메일_변경을_실패한다() {
      // given
      String email = faker.internet()
          .emailAddress();
      User user = builderWithEncoder.email(email)
          .password(password)
          .nickname(nickname)
          .build();
      userRepository.save(user);
      UpdateEmailRequest request = new UpdateEmailRequest(email);

      // when
      ThrowingCallable updateEmail = () -> userService.updateEmail(
          user.getId(), user.getId(), request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(updateEmail)
          .withMessage(UserErrorCode.DUPLICATE_EXISTING_EMAIL.getMessage());
    }

    @Test
    void 이미_존재하는_이메일로_변경하면_이메일_변경을_실패한다() {
      // given
      String email1 = faker.internet()
          .emailAddress();
      String email2 = faker.internet()
          .emailAddress();
      User user1 = builderWithEncoder.email(email1)
          .password(password)
          .nickname(nickname)
          .build();
      User user2 = builderWithEncoder.email(email2)
          .password(password)
          .nickname(nickname)
          .build();
      userRepository.saveAll(List.of(user1, user2));
      UpdateEmailRequest request = new UpdateEmailRequest(email2);

      // when
      ThrowingCallable updateEmail = () -> userService.updateEmail(
          user1.getId(), user1.getId(), request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(updateEmail)
          .withMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    void 이메일_변경에_성공한다() {
      // given
      String email = faker.internet()
          .emailAddress();
      User user = builderWithEncoder.email(email)
          .password(password)
          .nickname(nickname)
          .build();
      User savedUser = userRepository.save(user);
      Long userId = user.getId();

      String newEmail = faker.internet()
          .emailAddress();
      UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest(newEmail);

      // when
      userService.updateEmail(userId, userId, updateEmailRequest);

      // then
      User updatedUser = userRepository.findById(savedUser.getId())
          .get();
      assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }

  }

  @Nested
  class 비밀번호_변경_테스트 {

    @Test
    void 로그인한_사용자와_비밀번호_변경할_사용자가_다르면_변경을_실패한다() {
      // given
      long loginRandomId = faker.random()
          .nextLong();
      long randomId = faker.random()
          .nextLong();
      String newPassword = faker.internet()
          .password(8, 40, true, true, true);

      // when
      ThrowingCallable updatePassword = () -> userService.updatePassword(
          loginRandomId, randomId, new UpdatePasswordRequest(newPassword));

      // then
      assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(updatePassword)
          .withMessage(UserErrorCode.INVALID_AUTHENTICATION.getMessage());
    }

    @Test
    void 존재하지_않는_사용자_아이디_비밀번호_변경을_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong();
      String newPassword = faker.internet()
          .password(8, 40, true, true, true);

      // when
      ThrowingCallable updatePassword = () -> userService.updatePassword(
          randomId, randomId, new UpdatePasswordRequest(newPassword));

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(updatePassword)
          .withMessage(UserErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 변경할_비밀번호가_기존_비밀번호와_동일한_경우_비밀번호_변경을_실패한다() {
      // given
      String email = faker.internet()
          .emailAddress();
      User user = builderWithEncoder.email(email)
          .password(password)
          .nickname(nickname)
          .build();
      User savedUser = userRepository.save(user);
      Long userId = savedUser.getId();
      UpdatePasswordRequest request = new UpdatePasswordRequest(password);

      // when
      ThrowingCallable updatePassword = () -> userService.updatePassword(userId, userId, request);

      // then
      assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(updatePassword)
          .withMessage(UserErrorCode.DUPLICATE_EXISTING_PASSWORD.getMessage());
    }

    @Test
    void 비밀번호_변경을_성공한다() {
      // given
      String email = faker.internet()
          .emailAddress();
      String oldPassword = faker.internet()
          .password(8, 40, true, true, true);
      String newPassword = faker.internet()
          .password(8, 40, true, true, true);

      User user = builderWithEncoder.email(email)
          .password(oldPassword)
          .nickname(nickname)
          .build();
      User savedUser = userRepository.save(user);
      Long userId = savedUser.getId();
      UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(newPassword);

      // when
      userService.updatePassword(userId, userId, updatePasswordRequest);

      // then
      User updatedUser = userRepository.findById(savedUser.getId())
          .get();
      assertThat(updatedUser.getPassword()
          .check(newPassword, passwordEncoder)).isTrue();
    }

  }

}
