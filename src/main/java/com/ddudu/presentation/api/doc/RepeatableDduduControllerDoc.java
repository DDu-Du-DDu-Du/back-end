package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.repeatable_ddudu.requset.CreateRepeatableDduduRequest;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "반복 뚜두 관련 API")
public interface RepeatableDduduControllerDoc {

  @Operation(summary = "반복 뚜두 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = IdResponse.class)
      )
  )
  ResponseEntity<IdResponse> create(Long loginId, CreateRepeatableDduduRequest request);

}
