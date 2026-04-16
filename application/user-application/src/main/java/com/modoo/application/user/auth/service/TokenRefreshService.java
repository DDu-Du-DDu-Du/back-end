package com.modoo.application.user.auth.service;

import com.modoo.application.common.dto.auth.request.TokenRefreshRequest;
import com.modoo.application.common.dto.auth.response.TokenResponse;
import com.modoo.application.common.port.auth.in.TokenRefreshUseCase;
import com.modoo.application.common.port.auth.out.TokenLoaderPort;
import com.modoo.application.common.port.auth.out.TokenManipulationPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.application.user.auth.jwt.TokenManager;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.AuthErrorCode;
import com.modoo.domain.user.auth.aggregate.RefreshToken;
import com.modoo.domain.user.auth.aggregate.vo.UserFamily;
import com.modoo.domain.user.user.aggregate.User;
import java.time.LocalDateTime;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(noRollbackFor = {UnsupportedOperationException.class, SecurityException.class})
public class TokenRefreshService implements TokenRefreshUseCase {

  private final TokenLoaderPort tokenLoaderPort;
  private final TokenManipulationPort tokenManipulationPort;
  private final TokenManager tokenManager;
  private final UserLoaderPort userLoaderPort;

  @Override
  public TokenResponse refresh(TokenRefreshRequest request) {
    String requestRefreshToken = request.refreshToken();
    UserFamily decoded = decodeOrThrowUnauthorized(requestRefreshToken);
    String authority = decodeAuthorityOrThrowUnauthorized(requestRefreshToken);
    String accessToken = tokenManager.createAccessToken(decoded.getUserId(), authority);
    RefreshToken newRefreshToken = tokenManager.createRefreshToken(
        decoded.getUserId(),
        decoded.getFamily(),
        authority
    );
    LocalDateTime now = LocalDateTime.now();

    long updated = tokenManipulationPort.rotateIfCurrentMatches(
        decoded.getUserId(),
        decoded.getFamily(),
        requestRefreshToken,
        newRefreshToken.getTokenValue(),
        now
    );

    if (updated == 1L) {
      return new TokenResponse(accessToken, newRefreshToken.getTokenValue());
    }

    userLoaderPort.loadFullUser(decoded.getUserId())
        .orElseThrow(() -> new MissingResourceException(
            AuthErrorCode.USER_NOT_FOUND.getCodeName(),
            User.class.getCanonicalName(),
            String.valueOf(decoded.getUserId())
        ));

    Optional<RefreshToken> found = tokenLoaderPort.loadOneByUserFamily(
        decoded.getUserId(),
        decoded.getFamily()
    );

    RefreshToken saved = found.orElseThrow(() -> new MissingResourceException(
        AuthErrorCode.REFRESH_TOKEN_NOT_FOUND.getCodeName(),
        RefreshToken.class.getCanonicalName(),
        decoded.getUserFamilyValue()
    ));

    if (!saved.hasSamePreviousToken(requestRefreshToken)) {
      tokenManipulationPort.deleteByUserFamily(decoded.getUserId(), decoded.getFamily());
      throw new SecurityException(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
    }

    if (saved.isWithinGracePeriod(now)) {
      return new TokenResponse(accessToken, saved.getCurrentToken());
    }

    tokenManipulationPort.deleteByUserFamily(decoded.getUserId(), decoded.getFamily());
    throw new SecurityException(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  private UserFamily decodeOrThrowUnauthorized(String refreshToken) {
    try {
      return tokenManager.decodeRefreshToken(refreshToken);
    } catch (RuntimeException e) {
      throw new UnsupportedOperationException(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());
    }
  }

  private String decodeAuthorityOrThrowUnauthorized(String refreshToken) {
    try {
      String authority = tokenManager.decodeRefreshTokenAuthority(refreshToken);

      if (Objects.isNull(authority)) {
        throw new UnsupportedOperationException(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());
      }

      return authority;
    } catch (RuntimeException e) {
      throw new UnsupportedOperationException(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());
    }
  }

}
