package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatDduduRequest;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import com.ddudu.presentation.api.doc.error.AuthErrorExamples;
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
              responseCode = "400", description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "6001",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_BLANK_NAME
                      ),
                      @ExampleObject(
                          name = "6006",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_EXCESSIVE_NAME_LENGTH
                      ),
                      @ExampleObject(
                          name = "6002",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_NULL_GOAL_VALUE
                      ),
                      @ExampleObject(
                          name = "6003",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_NULL_REPEAT_TYPE
                      ),
                      @ExampleObject(
                          name = "6009",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_INVALID_REPEAT_TYPE
                      ),
                      @ExampleObject(
                          name = "6004",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_NULL_START_DATE
                      ),
                      @ExampleObject(
                          name = "6005",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_NULL_END_DATE
                      ),
                      @ExampleObject(
                          name = "6007",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_UNABLE_TO_END_BEFORE_START
                      ),
                      @ExampleObject(
                          name = "6008",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_UNABLE_TO_FINISH_BEFORE_BEGIN
                      ),
                      @ExampleObject(
                          name = "6010",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_INVALID_DAY_OF_WEEK
                      ),
                      @ExampleObject(
                          name = "6011",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_NULL_OR_EMPTY_REPEAT_DATES_OF_MONTH
                      ),
                      @ExampleObject(
                          name = "6012",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_NULL_OR_EMPTY_REPEAT_DAYS_OF_WEEK
                      ),
                      @ExampleObject(
                          name = "6013",
                          value = RepeatDduduErrorExamples.REPEAT_DDUDU_NULL_LAST_DAY
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401", description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403", description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "6014",
                      description = "해당 목표에 대한 권한이 없는 경우 (본인만 가능)",
                      value = GoalErrorExamples.GOAL_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404", description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "6014",
                      description = "목표 아이디가 유효하지 않은 경우",
                      value = RepeatDduduErrorExamples.REPEAT_DDUDU_INVALID_GOAL
                  )
              )
          )
      }
  )
  ResponseEntity<IdResponse> create(Long loginId, CreateRepeatDduduRequest request);

}
