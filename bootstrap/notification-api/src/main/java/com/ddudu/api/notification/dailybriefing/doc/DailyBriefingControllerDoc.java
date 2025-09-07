package com.ddudu.api.notification.dailybriefing.doc;

import com.ddudu.application.common.dto.notification.response.DailyBriefingResponse;
import com.ddudu.bootstrap.common.doc.examples.DailyBriefingErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Daily Briefing",
    description = "데일리 요약 관련 API"
)
public interface DailyBriefingControllerDoc {

  @Operation(summary = "데일리 브리핑")
  @ApiResponses(
      {
          @ApiResponse(
              responseCode = "201",
              description = "CREATED",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "11001",
                          value = DailyBriefingErrorExamples.NULL_USER_ID
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "11002",
                      value = DailyBriefingErrorExamples.LOGIN_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  ResponseEntity<DailyBriefingResponse> getDailyBriefing(Long loginId);

}
