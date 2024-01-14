package com.ddudu.user.controller;

import com.ddudu.common.annotation.Login;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.ToggleOptionResponse;
import com.ddudu.user.dto.response.UpdateEmailResponse;
import com.ddudu.user.dto.response.UpdatePasswordResponse;
import com.ddudu.user.dto.response.UserProfileResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  @GetMapping("/{id}")
  public ResponseEntity<UserProfileResponse> getById(
      @PathVariable
      Long id
  ) {
    UserProfileResponse response = userService.findById(id);

    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/email")
  public ResponseEntity<UpdateEmailResponse> updateEmail(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateEmailRequest request
  ) {
    UpdateEmailResponse response = userService.updateEmail(loginId, id, request);

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

  @PutMapping("/{id}/profile")
  public ResponseEntity<UserProfileResponse> updateProfile(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateProfileRequest request
  ) {
    UserProfileResponse response = userService.updateProfile(loginId, id, request);

    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/options")
  public ResponseEntity<ToggleOptionResponse> switchOption(
      @Login
      Long login,
      @PathVariable
      Long id
  ) {
    ToggleOptionResponse response = userService.switchOption(login, id);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/followees")
  public ResponseEntity<List<UserResponse>> getFollowees(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    List<UserResponse> responses = userService.findFollowees(loginId, id);

    return ResponseEntity.ok(responses);
  }

}
