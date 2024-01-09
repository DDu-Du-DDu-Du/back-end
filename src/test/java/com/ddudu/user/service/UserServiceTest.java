package com.ddudu.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.auth.jwt.converter.JwtConverter;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.common.exception.InvalidTokenException;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.repository.UserRepository;
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

    @BeforeEach
    void setUp() {
      builderWithEncoder = User.builder()
          .passwordEncoder(passwordEncoder);
      password = faker.internet()
          .password(8, 40, true, true, true);
      nickname = faker.oscarMovie()
          .character();
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
  class 사용자_단일_조회 {

    @Test
    void 존재하지_않는_사용자_아이디_단일_조회를_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong();

      // when
      ThrowingCallable login = () -> userService.findById(randomId);

      // then
      assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(login)
          .withMessage(UserErrorCode.INVALID_AUTHENTICATION.getMessage());
    }

    @Test
    void 사용자_단일_조회를_성공한다() {
      // given
      String email = faker.internet()
          .emailAddress();
      User user = builderWithEncoder
          .email(email)
          .password(password)
          .nickname(nickname)
          .build();
      User expected = userRepository.save(user);

      // when
      UserResponse actual = userService.findById(expected.getId());

      // then
      assertThat(actual.id()).isEqualTo(expected.getId());
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
      User user = createUser();

      String newNickname = faker.oscarMovie()
          .character();
      String newIntroduction = faker.book()
          .title();
      UpdateProfileRequest request = new UpdateProfileRequest(newNickname, newIntroduction);

      // when
      userService.updateProfile(user.getId(), user.getId(), request);

      // then
      User actual = userRepository.findById(user.getId())
          .get();
      assertThat(actual).extracting("nickname", "introduction")
          .containsExactly(newNickname, newIntroduction);
    }

    @Test
    void 로그인_사용자와_변경하고자_하는_프로필의_사용자가_다를_경우_프로필_수정에_실패한다() {
      // given
      Long invalidLoginId = faker.random()
          .nextLong();
      User user = createUser();
      UpdateProfileRequest request = new UpdateProfileRequest(nickname, introduction);

      // when
      ThrowingCallable updateProfile = () -> userService.updateProfile(
          invalidLoginId, user.getId(), request);

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(updateProfile)
          .withMessage(UserErrorCode.INVALID_AUTHORITY.getMessage());
    }

    private User createUser() {
      String email = faker.internet()
          .emailAddress();

      User user = builderWithEncoder
          .email(email)
          .password(password)
          .nickname(nickname)
          .build();

      return userRepository.save(user);
    }

  }

}
