package com.modoo.api.user.auth.controller;

import com.modoo.api.user.auth.doc.AuthControllerDoc;
import com.modoo.application.common.dto.auth.request.SocialRequest;
import com.modoo.application.common.dto.auth.request.TokenRefreshRequest;
import com.modoo.application.common.dto.auth.response.TokenResponse;
import com.modoo.application.common.port.auth.in.LogoutUseCase;
import com.modoo.application.common.port.auth.in.SocialLoginUseCase;
import com.modoo.application.common.port.auth.in.TokenRefreshUseCase;
import com.modoo.bootstrap.common.annotation.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDoc {

  private final SocialLoginUseCase socialLoginUseCase;
  private final TokenRefreshUseCase tokenRefreshUseCase;
  private final LogoutUseCase logoutUseCase;

  @PostMapping("/login/{providerType}")
  public ResponseEntity<TokenResponse> login(
      @RequestHeader(HttpHeaders.AUTHORIZATION)
      String socialToken,
      @PathVariable
      String providerType
  ) {
    SocialRequest request = new SocialRequest(socialToken, providerType);
    TokenResponse response = socialLoginUseCase.login(request);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/token")
  public ResponseEntity<TokenResponse> refresh(
      @RequestBody
      TokenRefreshRequest request
  ) {
    TokenResponse response = tokenRefreshUseCase.refresh(request);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<Void> logout(
      @Login
      Long loginUserId,
      @RequestHeader("Refresh-Token")
      String refreshToken
  ) {
    logoutUseCase.logout(loginUserId, refreshToken);

    return ResponseEntity.noContent()
        .build();
  }

}
