package com.ddudu.old.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.domain.user.domain.enums.Authority;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.jwt.JwtAuthToken;
import com.ddudu.presentation.api.resolver.LoginUserArgumentResolver;
import java.time.Instant;
import java.util.Collections;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class LoginUserArgumentResolverTest {

  static final Faker faker = new Faker();
  static final Class requireType = Long.class;

  @InjectMocks
  private LoginUserArgumentResolver loginResolver;
  @Mock
  SecurityContext securityContext;

  @Mock
  private MethodParameter parameter;

  @Test
  void 메서드_파라미터에_Login_애너테이션이_있고_변수_타입이_Long_이면_True_를_반환한다() {
    // given
    given(parameter.getParameterAnnotation(Login.class)).willReturn(mock(Login.class));
    given(parameter.getParameterType()).willReturn(requireType);

    // when
    boolean canSupport = loginResolver.supportsParameter(parameter);

    // then
    assertTrue(canSupport);
  }

  @Test
  void Login_애너테이션이_붙은_메서드_파라미터의_변수_타입이_Long이_아니면_False_를_반환한다() {
    // given
    Class invalidType = String.class;
    given(parameter.getParameterAnnotation(Login.class)).willReturn(mock(Login.class));
    given(parameter.getParameterType()).willReturn(invalidType);

    // when
    boolean canSupport = loginResolver.supportsParameter(parameter);

    // then
    assertFalse(canSupport);
  }

  @Test
  void Authentication이_유효하면_로그인_사용자ID_를_반환한다() {
    // given
    Long userId = faker.random()
        .nextLong();
    Jwt jwt = createJWT();
    JwtAuthToken token = new JwtAuthToken(jwt, Authority.NORMAL, userId);

    given(securityContext.getAuthentication()).willReturn(token);
    SecurityContextHolder.setContext(securityContext);

    // when
    Object actual = loginResolver.resolveArgument(null, null, null, null);

    // then
    assertThat(actual).isInstanceOf(Long.class);
    assertThat(actual).isEqualTo(userId);
  }

  @Test
  void Authentication이_null이면_InvalidAuthenticationException_예외를_반환한다() {
    // given
    given(securityContext.getAuthentication()).willReturn(null);
    SecurityContextHolder.setContext(securityContext);

    // when
    ThrowingCallable resolveArgument = () -> loginResolver.resolveArgument(null, null, null, null);

    // then
    assertThatThrownBy(resolveArgument).isInstanceOf(UnsupportedOperationException.class)
        .hasMessage(AuthErrorCode.BAD_TOKEN_CONTENT.getCodeName());
  }

  private static Jwt createJWT() {
    return new Jwt(
        "tokenValue", Instant.now(), Instant.now()
        .plusSeconds(300), Collections.singletonMap("header", "header"),
        Collections.singletonMap("claim", "claim")
    );
  }

}
