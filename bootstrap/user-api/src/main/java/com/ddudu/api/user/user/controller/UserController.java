package com.ddudu.api.user.user.controller;

import com.ddudu.api.user.user.doc.UserControllerDoc;
import com.ddudu.application.common.dto.user.response.MeResponse;
import com.ddudu.application.common.port.user.in.GetMyInfoUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
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

}
