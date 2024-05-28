package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.user.MeResponse;
import com.ddudu.old.user.domain.UserSearchType;
import com.ddudu.old.user.dto.FollowingSearchType;
import com.ddudu.old.user.dto.request.FollowRequest;
import com.ddudu.old.user.dto.request.UpdateFollowingRequest;
import com.ddudu.old.user.dto.request.UpdateProfileRequest;
import com.ddudu.old.user.dto.response.FollowingResponse;
import com.ddudu.old.user.dto.response.ToggleOptionResponse;
import com.ddudu.old.user.dto.response.UserProfileResponse;
import com.ddudu.old.user.dto.response.UsersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원 관련 API")
public interface UserControllerDoc {

  @Operation(summary = "내 정보 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = MeResponse.class)
      )
  )
  ResponseEntity<MeResponse> validateToken(Long loginId);

  @Operation(summary = "회원 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UserProfileResponse.class)
      )
  )
  ResponseEntity<UserProfileResponse> getById(Long id);

  @Operation(summary = "사용자 검색")
  ResponseEntity<List<UserProfileResponse>> search(String keyword, UserSearchType searchType);

  @Operation(summary = "프로필 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UserProfileResponse.class)
      )
  )
  ResponseEntity<UserProfileResponse> updateProfile(
      Long loginId, Long id, UpdateProfileRequest request
  );

  @Operation(summary = "사용자 옵션 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ToggleOptionResponse.class)
      )
  )
  ResponseEntity<ToggleOptionResponse> switchOption(Long loginId, Long id);

  @Operation(summary = "사용자 팔로잉 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UsersResponse.class)
      )
  )
  ResponseEntity<UsersResponse> getFromFollowings(
      Long loginId, Long id, FollowingSearchType searchType
  );

  @Operation(summary = "팔로잉 신청")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = FollowingResponse.class)
      )
  )
  ResponseEntity<FollowingResponse> createFollowing(Long loginId, Long id, FollowRequest request);

  @Operation(summary = "팔로잉 상태 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = FollowingResponse.class)
      )
  )
  ResponseEntity<FollowingResponse> updateFollowingStatus(
      Long loginId, Long id, Long followingId, UpdateFollowingRequest request
  );

  @Operation(summary = "팔로잉 거절")
  @ApiResponse(
      responseCode = "204"
  )
  ResponseEntity<Void> deleteFollowing(Long loginId, Long id, Long followingId);

}
