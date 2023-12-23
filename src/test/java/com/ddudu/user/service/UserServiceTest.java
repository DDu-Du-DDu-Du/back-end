package com.ddudu.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.repository.UserRepository;
import java.util.Optional;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

  UserBuilder builderWithEncoder;
  String password;
  String nickname;

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @Nested
  class 회원가입_테스트 {

    @BeforeEach
    void setUp() {
      builderWithEncoder = User.builder()
          .passwordEncoder(PASSWORD_ENCODER);
      password = faker.internet()
          .password(8, 40, false, true, true);
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

      SignUpRequest request = new SignUpRequest(null, user.getEmail(), password, nickname);

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
      SignUpRequest request = new SignUpRequest(username, differentEmail, password, nickname);

      // when
      ThrowingCallable signUp = () -> userService.signUp(request);

      // then
      assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(signUp)
          .withMessage("이미 존재하는 아이디입니다.");
    }

    @ParameterizedTest(name = "선택 아이디 : {0}")
    @NullSource
    @ValueSource(strings = "username")
    void 회원가입을_성공한다(String username) {
      // given
      String email = faker.internet()
          .emailAddress();
      SignUpRequest request = new SignUpRequest(username, email, password, nickname);

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
