package com.ddudu.user.controller;

import com.ddudu.auth.jwt.JwtAuthToken;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.UserProfileResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  public ResponseEntity<UserResponse> validateToken(Authentication authentication) {
    long userId = ((JwtAuthToken) authentication).getUserId();
    UserResponse response = userService.findById(userId);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}/profile")
  public ResponseEntity<UserProfileResponse> updateProfile(
      Authentication authentication,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateProfileRequest request
  ) {
    Long loginId = ((JwtAuthToken) authentication).getUserId();
    UserProfileResponse response = userService.updateProfile(loginId, id, request);

    return ResponseEntity.ok(response);
  }

}
