package com.ddudu.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.auth.dto.request.LoginRequest;
import com.ddudu.auth.dto.response.LoginResponse;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import java.util.Map;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
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

  @Nested
  class 로그인_테스트 {

    @BeforeEach
    void setUp() {
      email = faker.internet()
          .emailAddress();
      password = faker.internet()
          .password(8, 40, false, true, true);
    }

    @Test
    void 존재하지_않는_이메일_로그인을_실패한다() {
      // given
      LoginRequest request = new LoginRequest(email, password);

      // when
      ThrowingCallable login = () -> authService.login(request);

      // then
      assertThatIllegalArgumentException().isThrownBy(login)
          .withMessage("입력하신 이메일은 없는 이메일입니다.");
    }

    @Test
    void 비밀번호가_다를_시_로그인을_실패한다() {
      // given
      String nickname = faker.funnyName()
          .name();
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
          .withMessage("비밀번호가 일치하지 않습니다");
    }

    @Test
    void 로그인을_성공하고_JWT를_발급받는다() {
      // given
      String nickname = faker.funnyName()
          .name();
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
  }
}
