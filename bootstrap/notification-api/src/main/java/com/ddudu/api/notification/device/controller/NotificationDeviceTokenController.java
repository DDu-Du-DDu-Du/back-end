package com.ddudu.api.notification.device.controller;

import com.ddudu.api.notification.device.doc.NotificationDeviceTokenControllerDoc;
import com.ddudu.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.ddudu.application.common.dto.notification.response.SaveDeviceTokenResponse;
import com.ddudu.application.common.port.notification.in.SaveDeviceTokenUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-tokens")
@RequiredArgsConstructor
public class NotificationDeviceTokenController implements NotificationDeviceTokenControllerDoc {

  private final SaveDeviceTokenUseCase saveDeviceTokenUseCase;

  @Override
  @PostMapping
  public ResponseEntity<SaveDeviceTokenResponse> registerDeviceToken(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      SaveDeviceTokenRequest request
  ) {
    SaveDeviceTokenResponse response = saveDeviceTokenUseCase.save(loginId, request);

    return ResponseEntity.created(null)
        .body(response);
  }

}
