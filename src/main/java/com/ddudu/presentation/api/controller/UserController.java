package com.ddudu.presentation.api.controller;

import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
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
import com.ddudu.presentation.api.exception.ForbiddenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@Tag(name = "회원 관련 API")
public class UserController {

  private final UserService userService;
  private final FollowingService followingService;

  @GetMapping("/{id}")
  @Operation(summary = "회원 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UserProfileResponse.class)
      )
  )
  @Deprecated
  public ResponseEntity<UserProfileResponse> getById(
      @PathVariable
      Long id
  ) {
    UserProfileResponse response = userService.findById(id);

    return ResponseEntity.ok(response);
  }

  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "사용자 검색")
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
  @Operation(summary = "프로필 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UserProfileResponse.class)
      )
  )
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
  @Operation(summary = "사용자 옵션 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ToggleOptionResponse.class)
      )
  )
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
  @Operation(summary = "사용자 팔로잉 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UsersResponse.class)
      )
  )
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
  @Operation(summary = "팔로잉 신청")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = FollowingResponse.class)
      )
  )
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
  @Operation(summary = "팔로잉 상태 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = FollowingResponse.class)
      )
  )
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
  @Operation(summary = "팔로잉 거절")
  @ApiResponse(
      responseCode = "204"
  )
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
