package com.ddudu.old.auth.jwt;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.domain.user.domain.enums.Authority;
import com.ddudu.presentation.api.exception.InvalidTokenException;
import com.ddudu.presentation.api.jwt.JwtAuthToken;
import java.time.Instant;
import java.util.Collections;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

@DisplayNameGeneration(ReplaceUnderscores.class)
class JwtAuthTokenTest {

  @Test
  void 인증_토큰이_잘못_생성되어_사용자_정보가_없으면_예외를_반환한다() {
    // given
    Jwt jwt = new Jwt(
        "tokenValue", Instant.now(), Instant.now()
        .plusSeconds(300), Collections.singletonMap("header", "header"),
        Collections.singletonMap("claim", "claim")
    );
    JwtAuthToken token = new JwtAuthToken(jwt, Authority.NORMAL, null);

    // when
    ThrowingCallable getUserId = token::getUserId;

    // then
    assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(getUserId)
        .withMessage(AuthErrorCode.BAD_TOKEN_CONTENT.getMessage());
  }

}
