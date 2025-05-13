package com.ddudu.api.user.auth.resolver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ddudu.api.user.auth.jwt.AuthorityProxy;
import com.ddudu.api.user.auth.jwt.JwtAuthToken;
import com.ddudu.bootstrap.common.annotation.Login;
import com.ddudu.common.exception.AuthErrorCode;
import java.time.Instant;
import java.util.Collections;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    BDDMockito.given(parameter.getParameterAnnotation(Login.class))
        .willReturn(Mockito.mock(Login.class));
    BDDMockito.given(parameter.getParameterType())
        .willReturn(requireType);

    // when
    boolean canSupport = loginResolver.supportsParameter(parameter);

    // then
    assertTrue(canSupport);
  }

  @Test
  void Login_애너테이션이_붙은_메서드_파라미터의_변수_타입이_Long이_아니면_False_를_반환한다() {
    // given
    Class invalidType = String.class;
    BDDMockito.given(parameter.getParameterAnnotation(Login.class))
        .willReturn(Mockito.mock(Login.class));
    BDDMockito.given(parameter.getParameterType())
        .willReturn(invalidType);

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
    JwtAuthToken token = new JwtAuthToken(jwt, AuthorityProxy.NORMAL, userId);

    BDDMockito.given(securityContext.getAuthentication())
        .willReturn(token);
    SecurityContextHolder.setContext(securityContext);

    // when
    Object actual = loginResolver.resolveArgument(null, null, null, null);

    // then
    Assertions.assertThat(actual)
        .isInstanceOf(Long.class);
    Assertions.assertThat(actual)
        .isEqualTo(userId);
  }

  @Test
  void Authentication이_null이면_InvalidAuthenticationException_예외를_반환한다() {
    // given
    BDDMockito.given(securityContext.getAuthentication())
        .willReturn(null);
    SecurityContextHolder.setContext(securityContext);

    // when
    ThrowingCallable resolveArgument = () -> loginResolver.resolveArgument(null, null, null, null);

    // then
    Assertions.assertThatThrownBy(resolveArgument)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage(AuthErrorCode.BAD_TOKEN_CONTENT.getCodeName());
  }

  private static Jwt createJWT() {
    return new Jwt(
        "tokenValue",
        Instant.now(),
        Instant.now()
            .plusSeconds(300),
        Collections.singletonMap("header", "header"),
        Collections.singletonMap("claim", "claim")
    );
  }

}
