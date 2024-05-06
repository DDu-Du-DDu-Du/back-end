package com.ddudu.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.old.auth.domain.authority.Authority;
import com.ddudu.old.auth.dto.request.LoginRequest;
import com.ddudu.old.auth.dto.response.LoginResponse;
import com.ddudu.old.auth.dto.response.MeResponse;
import com.ddudu.old.auth.exception.AuthErrorCode;
import com.ddudu.old.auth.service.AuthService;
import com.ddudu.presentation.api.exception.BadCredentialsException;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.InvalidTokenException;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.user.domain.UserRepository;
import java.util.Map;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthServiceTest {

  static final Faker faker = new Faker();

  @Autowired
  AuthService authService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  JwtDecoder jwtDecoder;

  String email;
  String password;
  String nickname;

  @BeforeEach
  void setUp() {
    email = faker.internet()
        .emailAddress();
    password = faker.internet()
        .password(8, 40, true, true, true);
    nickname = faker.internet()
        .username();
  }

  @Nested
  @Disabled
  class 로그인_테스트 {

    @Test
    void 로그인을_성공하고_JWT를_발급받는다() {
      // given
      User user = User.builder()
          .email(email)
          .password(password)
          .nickname(nickname)
          .passwordEncoder(passwordEncoder)
          .build();
      User expected = userRepository.save(user);
      LoginRequest request = new LoginRequest(email, password);

      // when
      LoginResponse response = authService.login(request);

      // then
      Jwt decoded = jwtDecoder.decode(response.accessToken());
      Map<String, Object> claims = decoded.getClaims();
      Object actualId = claims.get("user");
      Authority actualAuthority = Authority.valueOf((String) claims.get("auth"));

      assertThat(actualId).isEqualTo(expected.getId());
      assertThat(actualAuthority).isEqualTo(expected.getAuthority());
    }

    @Test
    void 존재하지_않는_이메일_로그인을_실패한다() {
      // given
      LoginRequest request = new LoginRequest(email, password);

      // when
      ThrowingCallable login = () -> authService.login(request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(login)
          .withMessage(AuthErrorCode.EMAIL_NOT_EXISTING.getMessage());
    }

    @Test
    void 비밀번호가_다를_시_로그인을_실패한다() {
      // given
      User user = User.builder()
          .email(email)
          .passwordEncoder(passwordEncoder)
          .password(password)
          .nickname(nickname)
          .build();

      userRepository.save(user);

      String newPassword = faker.internet()
          .password();
      LoginRequest request = new LoginRequest(email, newPassword);

      // when
      ThrowingCallable login = () -> authService.login(request);

      // then
      assertThatExceptionOfType(BadCredentialsException.class).isThrownBy(login)
          .withMessage(AuthErrorCode.BAD_CREDENTIALS.getMessage());
    }

  }

  @Nested
  class 사용자_로딩_테스트 {

    @Test
    void 사용자_로딩을_성공한다() {
      // given
      User user = User.builder()
          .passwordEncoder(passwordEncoder)
          .email(email)
          .password(password)
          .nickname(nickname)
          .build();
      User expected = userRepository.save(user);

      // when
      MeResponse actual = authService.loadUser(expected.getId());

      // then
      assertThat(actual.id()).isEqualTo(expected.getId());
    }

    @Test
    void 존재하지_않는_사용자_아이디_로딩을_실패한다() {
      // given
      long randomId = faker.random()
          .nextLong();

      // when
      ThrowingCallable login = () -> authService.loadUser(randomId);

      // then
      assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(login)
          .withMessage(AuthErrorCode.INVALID_AUTHENTICATION.getMessage());
    }

  }

}
