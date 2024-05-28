package com.ddudu.presentation.api.controller;

import com.ddudu.application.dto.authentication.request.SocialRequest;
import com.ddudu.application.dto.authentication.request.TokenRefreshRequest;
import com.ddudu.application.dto.authentication.response.TokenResponse;
import com.ddudu.application.port.in.auth.SocialLoginUseCase;
import com.ddudu.application.port.in.auth.TokenRefreshUseCase;
import com.ddudu.presentation.api.doc.AuthControllerDoc;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/login/{providerType}")
  public ResponseEntity<TokenResponse> login(
      @RequestHeader(HttpHeaders.AUTHORIZATION)
      @Parameter(hidden = true)
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

}
