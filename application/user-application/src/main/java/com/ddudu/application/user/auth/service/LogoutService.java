package com.ddudu.application.user.auth.service;

import com.ddudu.application.common.port.auth.in.LogoutUseCase;
import com.ddudu.application.common.port.auth.out.TokenManipulationPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.application.user.auth.jwt.TokenManager;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.AuthErrorCode;
import com.ddudu.domain.user.auth.aggregate.vo.UserFamily;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TokenManipulationPort tokenManipulationPort;
  private final TokenManager tokenManager;

  @Override
  public void logout(Long loginUserId, String refreshToken) {
    validateUserExists(loginUserId);

    UserFamily decoded = tokenManager.decodeRefreshToken(refreshToken);
    validateOwner(loginUserId, decoded.getUserId());

    tokenManipulationPort.deleteByUserFamily(decoded.getUserId(), decoded.getFamily());
  }

  private void validateUserExists(Long loginUserId) {
    userLoaderPort.getUserOrElseThrow(loginUserId, AuthErrorCode.USER_NOT_FOUND.getCodeName());
  }

  private void validateOwner(Long loginUserId, Long tokenUserId) {
    if (!Objects.equals(loginUserId, tokenUserId)) {
      throw new SecurityException(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

}
