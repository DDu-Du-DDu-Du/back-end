package com.ddudu.user.controller;

import com.ddudu.common.annotation.Login;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.UpdatePasswordResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @PatchMapping("/{id}/email")
  public ResponseEntity<UserResponse> updateEmail(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateEmailRequest request
  ) {
    UserResponse response = userService.updateEmail(loginId, id, request);

    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/password")
  public ResponseEntity<UpdatePasswordResponse> updatePassword(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdatePasswordRequest request
  ) {
    UpdatePasswordResponse response = userService.updatePassword(loginId, id, request);

    return ResponseEntity.ok(response);
  }

}
