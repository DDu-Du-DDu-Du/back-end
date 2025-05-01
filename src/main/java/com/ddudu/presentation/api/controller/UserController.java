package com.ddudu.presentation.api.controller;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.dto.user.MeResponse;
import com.ddudu.application.port.in.user.GetMyInfoUseCase;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.doc.UserControllerDoc;
import com.ddudu.presentation.api.exception.ForbiddenException;
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
