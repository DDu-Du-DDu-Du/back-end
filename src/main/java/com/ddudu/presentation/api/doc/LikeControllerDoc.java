package com.ddudu.presentation.api.doc;

import com.ddudu.old.like.dto.request.LikeRequest;
import com.ddudu.old.like.dto.response.LikeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "좋아요 관련 API")
public interface LikeControllerDoc {

  @Operation(summary = "좋아요/취소")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = LikeResponse.class)
      )
  )
  ResponseEntity<LikeResponse> toggle(Long loginId, LikeRequest request);

}
