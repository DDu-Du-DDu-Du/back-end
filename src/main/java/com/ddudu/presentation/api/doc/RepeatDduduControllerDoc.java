package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatDduduRequest;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import com.ddudu.presentation.api.doc.error.GoalErrorExamples;
import com.ddudu.presentation.api.doc.error.RepeatDduduErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Repeat Ddudu", description = "반복 뚜두 관련 API")
public interface RepeatDduduControllerDoc {

  @Operation(summary = "반복 뚜두 생성")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "201", description = "CREATED", useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "403", description = "FORBIDDEN",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "6014",
                          description = "해당 목표에 대한 권한이 없는 경우 (본인만 가능)",
                          value = GoalErrorExamples.GOAL_INVALID_AUTHORITY
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "404", description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "6014",
                          description = "목표 아이디가 유효하지 않은 경우",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_INVALID_GOAL
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<IdResponse> create(Long loginId, CreateRepeatDduduRequest request);

}
