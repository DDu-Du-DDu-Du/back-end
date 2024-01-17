package com.ddudu.user.controller;

import com.ddudu.common.annotation.Login;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.user.dto.request.FollowRequest;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdateFollowingRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.FollowingResponse;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.ToggleOptionResponse;
import com.ddudu.user.dto.response.UpdateEmailResponse;
import com.ddudu.user.dto.response.UpdatePasswordResponse;
import com.ddudu.user.dto.response.UserProfileResponse;
import com.ddudu.user.dto.response.UsersResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.service.FollowingService;
import com.ddudu.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private final FollowingService followingService;

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
    checkAuthority(loginId, id);

    UpdateEmailResponse response = userService.updateEmail(id, request);

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
    checkAuthority(loginId, id);

    UpdatePasswordResponse response = userService.updatePassword(id, request);

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
    checkAuthority(loginId, id);

    UserProfileResponse response = userService.updateProfile(id, request);

    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/options")
  public ResponseEntity<ToggleOptionResponse> switchOption(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    checkAuthority(loginId, id);

    ToggleOptionResponse response = userService.switchOption(id);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/followees")
  public ResponseEntity<UsersResponse> getFollowees(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    checkAuthority(loginId, id);

    UsersResponse responses = userService.findFollowees(id);

    return ResponseEntity.ok(responses);
  }

  @PostMapping("/{id}/followings")
  public ResponseEntity<FollowingResponse> createFollowing(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      FollowRequest request
  ) {
    checkAuthority(loginId, id);

    FollowingResponse response = followingService.create(id, request);
    URI uri = URI.create("/api/users/" + loginId + "/followings");

    return ResponseEntity.created(uri)
        .body(response);
  }

  @PutMapping("/{id}/followings/{followingId}")
  public ResponseEntity<FollowingResponse> updateFollowingStatus(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @PathVariable
      Long followingId,
      @RequestBody
      @Valid
      UpdateFollowingRequest request
  ) {
    checkAuthority(loginId, id);

    FollowingResponse response = followingService.updateStatus(id, followingId, request);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}/followings/{followingId}")
  public ResponseEntity<Void> deleteFollowing(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @PathVariable
      Long followingId
  ) {
    checkAuthority(loginId, id);
    followingService.delete(id, followingId);

    return ResponseEntity.noContent()
        .build();
  }

  private void checkAuthority(Long loginId, Long id) {
    if (!Objects.equals(loginId, id)) {
      throw new ForbiddenException(UserErrorCode.INVALID_AUTHORITY);
    }
  }

}
