package com.ddudu.auth.controller;

import com.ddudu.auth.dto.request.LoginRequest;
import com.ddudu.auth.dto.response.LoginResponse;
import com.ddudu.auth.dto.response.MeResponse;
import com.ddudu.auth.service.AuthService;
import com.ddudu.common.annotation.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestBody
      @Valid
      LoginRequest request
  ) {
    LoginResponse response = authService.login(request);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public ResponseEntity<MeResponse> validateToken(
      @Login
      Long loginId
  ) {
    MeResponse response = authService.loadUser(loginId);

    return ResponseEntity.ok(response);
  }

}
