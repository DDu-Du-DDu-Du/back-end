package com.modoo.api.notification.device.controller;

import com.modoo.api.notification.device.doc.NotificationDeviceTokenControllerDoc;
import com.modoo.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.modoo.application.common.dto.notification.response.SaveDeviceTokenResponse;
import com.modoo.application.common.port.notification.in.SaveDeviceTokenUseCase;
import com.modoo.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-tokens")
@RequiredArgsConstructor
public class NotificationDeviceTokenController implements NotificationDeviceTokenControllerDoc {

  private final SaveDeviceTokenUseCase saveDeviceTokenUseCase;

  @Override
  @PutMapping
  public ResponseEntity<SaveDeviceTokenResponse> registerDeviceToken(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      SaveDeviceTokenRequest request
  ) {
    SaveDeviceTokenResponse response = saveDeviceTokenUseCase.save(loginId, request);

    return ResponseEntity.ok()
        .body(response);
  }

}
