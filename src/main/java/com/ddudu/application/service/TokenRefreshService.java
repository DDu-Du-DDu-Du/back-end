package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.domain.authentication.dto.request.TokenRefreshRequest;
import com.ddudu.application.domain.authentication.dto.response.TokenResponse;
import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.domain.authentication.service.AuthDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.TokenRefreshUseCase;
import com.ddudu.application.port.out.TokenLoaderPort;
import com.ddudu.application.port.out.TokenManipulationPort;
import com.ddudu.application.port.out.UserLoaderPort;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional(dontRollbackOn = UnsupportedOperationException.class)
public class TokenRefreshService implements TokenRefreshUseCase {

  private final TokenLoaderPort tokenLoaderPort;
  private final TokenManipulationPort tokenManipulationPort;
  private final AuthDomainService authDomainService;
  private final UserLoaderPort userLoaderPort;

  @Override
  public TokenResponse refresh(TokenRefreshRequest request) {
    RefreshToken decoded = authDomainService.decodeRequestRefreshToken(request.refreshToken());
    List<RefreshToken> tokenFamily = tokenLoaderPort.loadByUserFamily(
        decoded.getUserId(), decoded.getFamily());
    RefreshToken currentRefreshToken = tokenFamily.stream()
        .filter(token -> token.hasSameTokenValue(decoded.getTokenValue()))
        .findFirst()
        .orElseThrow(() -> new UnsupportedOperationException(
            AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName()));

    validateNotUsed(tokenFamily, currentRefreshToken);

    User user = userLoaderPort.loadUserById(decoded.getUserId())
        .orElseThrow(() -> new NoSuchElementException(AuthErrorCode.USER_NOT_FOUND.getCodeName()));

    String accessToken = authDomainService.createAccessToken(user);
    RefreshToken newRefreshToken = authDomainService.createRefreshToken(
        user, decoded.getFamily());

    tokenManipulationPort.save(newRefreshToken);

    return new TokenResponse(accessToken, newRefreshToken.getTokenValue());
  }


  private void validateNotUsed(List<RefreshToken> tokenFamily, RefreshToken refreshToken) {
    if (tokenFamily.size() == 1) {
      return;
    }

    Long mostRecent = tokenFamily.stream()
        .map(RefreshToken::getId)
        .findFirst()
        .get();

    if (!refreshToken.isMostRecentInFamily(mostRecent)) {
      tokenManipulationPort.deleteAllFamily(tokenFamily);

      throw new UnsupportedOperationException(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());
    }
  }

}
