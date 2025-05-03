package com.ddudu.bootstrap.userapi.user.controller;

import com.ddudu.bootstrap.userapi.user.doc.UserControllerDoc;
import com.ddudu.domain.user.auth.exception.AuthErrorCode;
import com.ddudu.application.user.user.dto.response.MeResponse;
import com.ddudu.application.user.user.port.in.GetMyInfoUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import com.ddudu.bootstrap.common.exception.ForbiddenException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDoc {

  private final GetMyInfoUseCase getMyInfoUseCase;

  @GetMapping("/me")
  public ResponseEntity<MeResponse> validateToken(
      @Login
      Long loginId
  ) {
    MeResponse response = getMyInfoUseCase.getMyInfo(loginId);

    return ResponseEntity.ok(response);
  }

  private void checkAuthority(Long loginId, Long id) {
    if (!Objects.equals(loginId, id)) {
      throw new ForbiddenException(AuthErrorCode.INVALID_AUTHORITY);
    }
  }

}
