package com.ddudu.user.controller;

import com.ddudu.auth.jwt.JwtAuthToken;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<SignUpResponse> signUp(
      @RequestBody
      @Valid
      SignUpRequest request
  ) {
    SignUpResponse response = userService.signUp(request);
    URI uri = URI.create("/api/users/" + response.id());

    return ResponseEntity.created(uri)
        .body(response);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> validateToken() {
    JwtAuthToken token = (JwtAuthToken) SecurityContextHolder.getContext()
        .getAuthentication();
    UserResponse response = userService.loadFromToken(token);

    return ResponseEntity.ok(response);
  }

}
