package com.ddudu.presentation.api.controller;

import com.ddudu.application.domain.authentication.dto.request.SocialRequest;
import com.ddudu.application.domain.authentication.dto.response.TokenResponse;
import com.ddudu.application.port.in.SocialLoginUseCase;
import com.ddudu.old.auth.dto.response.MeResponse;
import com.ddudu.old.auth.service.AuthService;
import com.ddudu.presentation.api.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 관련 API")
public class AuthController {

  private final SocialLoginUseCase socialLoginUseCase;
  private final AuthService authService;

  @PostMapping("/login/{providerType}")
  @Operation(summary = "소셜 로그인")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TokenResponse.class)
      )
  )
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

  @GetMapping("/me")
  @Operation(summary = "내 정보 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = MeResponse.class)
      )
  )
  @Deprecated
  public ResponseEntity<MeResponse> validateToken(
      @Login
      Long loginId
  ) {
    MeResponse response = authService.loadUser(loginId);

    return ResponseEntity.ok(response);
  }

}
