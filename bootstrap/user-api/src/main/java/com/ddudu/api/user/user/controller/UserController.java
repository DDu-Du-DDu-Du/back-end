package com.ddudu.api.user.user.controller;

import com.ddudu.api.user.user.doc.UserControllerDoc;
import com.ddudu.application.common.dto.user.request.UpdateUserSettingsRequest;
import com.ddudu.application.common.dto.user.response.MeResponse;
import com.ddudu.application.common.dto.user.response.UserSettingsResponse;
import com.ddudu.application.common.port.user.in.GetMyInfoUseCase;
import com.ddudu.application.common.port.user.in.GetUserSettingsUseCase;
import com.ddudu.application.common.port.user.in.UpdateUserSettingsUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDoc {

  private final GetMyInfoUseCase getMyInfoUseCase;
  private final UpdateUserSettingsUseCase updateUserSettingsUseCase;
  private final GetUserSettingsUseCase getUserSettingsUseCase;

  @GetMapping("/settings")
  public ResponseEntity<UserSettingsResponse> getUserSettings(
      @Login
      Long loginId
  ) {
    UserSettingsResponse response = getUserSettingsUseCase.getUserSettings(loginId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public ResponseEntity<MeResponse> validateToken(
      @Login
      Long loginId
  ) {
    MeResponse response = getMyInfoUseCase.getMyInfo(loginId);

    return ResponseEntity.ok(response);
  }

  @Override
  @PutMapping("/settings")
  public ResponseEntity<UserSettingsResponse> updateSettings(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      UpdateUserSettingsRequest request
  ) {
    UserSettingsResponse response = updateUserSettingsUseCase.update(loginId, request);

    return ResponseEntity.ok(response);
  }

}
