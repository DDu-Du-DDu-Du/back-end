package com.ddudu.presentation.api.controller;

import com.ddudu.old.auth.dto.request.LoginRequest;
import com.ddudu.old.auth.dto.response.LoginResponse;
import com.ddudu.old.auth.dto.response.MeResponse;
import com.ddudu.old.auth.service.AuthService;
import com.ddudu.presentation.api.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 관련 API")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @Operation(summary = "로그인")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = LoginResponse.class)
      )
  )
  @Deprecated
  public ResponseEntity<LoginResponse> login(
      @RequestBody
      @Valid
      LoginRequest request
  ) {
    LoginResponse response = authService.login(request);

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
