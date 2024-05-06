package com.ddudu.presentation.api.controller;

import com.ddudu.old.like.dto.request.LikeRequest;
import com.ddudu.old.like.dto.response.LikeResponse;
import com.ddudu.old.like.service.LikeService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@Tag(name = "좋아요 관련 API")
public class LikeController {

  private final LikeService likeService;

  @PostMapping
  @Operation(summary = "좋아요/취소")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = LikeResponse.class)
      )
  )
  @Deprecated
  public ResponseEntity<LikeResponse> toggle(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      LikeRequest request
  ) {
    LikeResponse response = likeService.toggle(loginId, request);

    return ResponseEntity.ok(response);
  }

}
