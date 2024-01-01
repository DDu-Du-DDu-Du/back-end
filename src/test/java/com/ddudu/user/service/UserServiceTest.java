package com.ddudu.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserServiceTest {

  static final Faker faker = new Faker();
  static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
  String password;
  String nickname;
  UserBuilder builderWithEncoder;
  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

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
          .passwordEncoder(PASSWORD_ENCODER);
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
      assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(signUp)
          .withMessage("이미 존재하는 이메일입니다.");
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
      assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(signUp)
          .withMessage("이미 존재하는 아이디입니다.");
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

}
