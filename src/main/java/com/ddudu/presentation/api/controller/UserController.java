package com.ddudu.presentation.api.controller;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.dto.user.MeResponse;
import com.ddudu.application.port.in.user.GetMyInfoUseCase;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import com.ddudu.old.user.dto.request.FollowRequest;
import com.ddudu.old.user.dto.request.UpdateFollowingRequest;
import com.ddudu.old.user.dto.request.UpdateProfileRequest;
import com.ddudu.old.user.dto.response.FollowingResponse;
import com.ddudu.old.user.dto.response.ToggleOptionResponse;
import com.ddudu.old.user.dto.response.UserProfileResponse;
import com.ddudu.old.user.dto.response.UsersResponse;
import com.ddudu.old.user.service.FollowingService;
import com.ddudu.old.user.service.UserService;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.doc.UserControllerDoc;
import com.ddudu.presentation.api.exception.ForbiddenException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDoc {

  private final GetMyInfoUseCase getMyInfoUseCase;
  private final UserService userService;
  private final FollowingService followingService;

  @GetMapping("/me")
  public ResponseEntity<MeResponse> validateToken(
      @Login
      Long loginId
  ) {
    MeResponse response = getMyInfoUseCase.getMyInfo(loginId);

    return ResponseEntity.ok(response);
  }


  @GetMapping("/{id}")
  @Deprecated
  public ResponseEntity<UserProfileResponse> getById(
      @PathVariable
      Long id
  ) {
    UserProfileResponse response = userService.findById(id);

    return ResponseEntity.ok(response);
  }

  @GetMapping()
  @Deprecated
  public ResponseEntity<List<UserProfileResponse>> search(
      String keyword,
      @RequestParam(required = false)
      UserSearchType searchType
  ) {
    List<UserProfileResponse> response = userService.search(keyword, searchType);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}/profile")
  @Deprecated
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
  @Deprecated
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

  @GetMapping("/{id}/followings")
  @Deprecated
  public ResponseEntity<UsersResponse> getFromFollowings(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestParam
      FollowingSearchType searchType
  ) {
    checkAuthority(loginId, id);

    UsersResponse response = userService.findFromFollowings(id, searchType);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/followings")
  @Deprecated
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
  @Deprecated
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
  @Deprecated
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
      throw new ForbiddenException(AuthErrorCode.INVALID_AUTHORITY);
    }
  }

}
