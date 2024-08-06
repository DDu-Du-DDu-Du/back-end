package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.goal.request.ChangeGoalStatusRequest;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.dto.goal.request.UpdateGoalRequest;
import com.ddudu.application.dto.goal.response.BasicGoalWithStatusResponse;
import com.ddudu.application.dto.goal.response.GoalIdResponse;
import com.ddudu.application.dto.goal.response.GoalStatsCompletionNumberResponse;
import com.ddudu.application.dto.goal.response.GoalWithRepeatDduduResponse;
import com.ddudu.presentation.api.doc.error.AuthErrorExamples;
import com.ddudu.presentation.api.doc.error.GoalErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Goal",
    description = "목표 관련 API"
)
public interface GoalControllerDoc {

  @Operation(summary = "목표 생성")
  @ApiResponses(
      value = {
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
                          name = "3001",
                          value = GoalErrorExamples.GOAL_BLANK_NAME
                      ),
                      @ExampleObject(
                          name = "3002",
                          value = GoalErrorExamples.GOAL_EXCESSIVE_NAME_LENGTH
                      ),
                      @ExampleObject(
                          name = "3003",
                          value = GoalErrorExamples.GOAL_INVALID_COLOR_FORMAT
                      ),
                      @ExampleObject(
                          name = "3010",
                          value = GoalErrorExamples.GOAL_INVALID_PRIVACY_TYPE
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "3008",
                          description = "로그인 사용자 아이디가 유효하지 않는 경우",
                          value = GoalErrorExamples.GOAL_USER_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  ResponseEntity<GoalIdResponse> create(Long userId, CreateGoalRequest request);

  @Operation(summary = "목표 수정")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "3001",
                          value = GoalErrorExamples.GOAL_BLANK_NAME
                      ),
                      @ExampleObject(
                          name = "3002",
                          value = GoalErrorExamples.GOAL_EXCESSIVE_NAME_LENGTH
                      ),
                      @ExampleObject(
                          name = "3003",
                          value = GoalErrorExamples.GOAL_INVALID_COLOR_FORMAT
                      ),
                      @ExampleObject(
                          name = "3007",
                          value = GoalErrorExamples.GOAL_NULL_PRIVACY_TYPE
                      ),
                      @ExampleObject(
                          name = "3007",
                          value = GoalErrorExamples.GOAL_NULL_PRIVACY_TYPE
                      ),
                      @ExampleObject(
                          name = "3010",
                          value = GoalErrorExamples.GOAL_INVALID_PRIVACY_TYPE
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3009",
                      description = "해당 목표에 대한 권한이 없는 경우 (본인만 가능)",
                      value = GoalErrorExamples.GOAL_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "3004",
                          description = "목표 아이디가 유효하지 않는 경우",
                          value = GoalErrorExamples.GOAL_ID_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  @Parameter(
      name = "id",
      description = "수정할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<GoalIdResponse> update(Long loginId, Long id, UpdateGoalRequest request);

  @Operation(summary = "목표 상태 변경")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "BAD_REQUEST",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3005",
                      value = GoalErrorExamples.GOAL_NULL_STATUS
                  )
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "3009",
                          description = "해당 목표에 대한 권한이 없는 경우 (본인만 가능)",
                          value = GoalErrorExamples.GOAL_INVALID_AUTHORITY
                      )
                  }
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = {
                      @ExampleObject(
                          name = "3004",
                          description = "목표 아이디가 유효하지 않는 경우",
                          value = GoalErrorExamples.GOAL_ID_NOT_EXISTING
                      )
                  }
              )
          )
      }
  )
  @Parameter(
      name = "id",
      description = "상태를 변경할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<GoalIdResponse> changeStatus(
      Long loginId, Long id, ChangeGoalStatusRequest request
  );

  @Operation(summary = "목표 상세 조회")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3009",
                      description = "해당 목표에 대한 권한이 없는 경우 (본인만 가능)",
                      value = GoalErrorExamples.GOAL_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3004",
                      description = "목표 아이디가 유효하지 않는 경우",
                      value = GoalErrorExamples.GOAL_ID_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameter(
      name = "id",
      description = "조회할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<GoalWithRepeatDduduResponse> getById(Long loginId, Long id);

  @Operation(summary = "목표 전체 조회")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "OK",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3009",
                      description = "해당 목표에 대한 권한이 없는 경우 (본인만 가능)",
                      value = GoalErrorExamples.GOAL_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3008",
                      description = "로그인 사용자가 유효하지 않는 경우",
                      value = GoalErrorExamples.GOAL_USER_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameter(
      name = "userId",
      description = "조회할 목표의 소유자",
      in = ParameterIn.QUERY
  )
  ResponseEntity<List<BasicGoalWithStatusResponse>> getAllByUser(Long loginId, Long userId);

  @Operation(
      summary = "목표 삭제",
      description = "목표 삭제 시 하위 뚜두도 함께 삭제 됩니다."
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "NO_CONTENT",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "401",
              description = "UNAUTHORIZED",
              content = @Content(
                  examples = @ExampleObject(
                      name = "5002",
                      value = AuthErrorExamples.AUTH_BAD_TOKEN_CONTENT
                  )
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "FORBIDDEN",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3009",
                      description = "해당 목표에 대한 권한이 없는 경우 (본인만 가능)",
                      value = GoalErrorExamples.GOAL_INVALID_AUTHORITY
                  )
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "NOT_FOUND",
              content = @Content(
                  examples = @ExampleObject(
                      name = "3004",
                      description = "목표 아이디가 유효하지 않는 경우",
                      value = GoalErrorExamples.GOAL_ID_NOT_EXISTING
                  )
              )
          )
      }
  )
  @Parameter(
      name = "id",
      description = "삭제할 목표의 식별자",
      in = ParameterIn.PATH
  )
  ResponseEntity<Void> delete(Long loginId, Long id);

  @Operation(summary = "월별 달성 뚜두 수 통계. Not Yet Implemented")
  @ApiResponse(
      responseCode = "200"
  )
  ResponseEntity<List<GoalStatsCompletionNumberResponse>> collectNumberStats(
      Long loginId, YearMonth yearMonth
  );

}
