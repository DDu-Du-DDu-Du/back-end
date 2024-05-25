package com.ddudu.presentation.api.doc;

import com.ddudu.application.domain.ddudu.dto.request.MoveDateRequest;
import com.ddudu.application.domain.ddudu.dto.request.PeriodSetupRequest;
import com.ddudu.application.domain.ddudu.dto.response.DduduInfo;
import com.ddudu.application.domain.ddudu.dto.response.TimetableResponse;
import com.ddudu.old.todo.dto.request.CreateTodoRequest;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "뚜두 관련 API")
public interface DduduControllerDoc {

  @Operation(summary = "뚜두 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = DduduInfo.class)
      )
  )
  ResponseEntity<DduduInfo> create(Long loginId, CreateTodoRequest request);

  @Operation(summary = "뚜두 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoResponse.class)
      )
  )
  ResponseEntity<TodoResponse> getById(Long loginId, Long id);

  @Operation(summary = "일간 뚜두 조회 (목표별 / 시간별)")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TimetableResponse.class)
      )
  )
  @Parameters(
      {
          @Parameter(
              name = "userId",
              description = "조회할 뚜두의 사용자 식별자 (기본값: 로그인한 사용자)",
              in = ParameterIn.QUERY
          ),
          @Parameter(
              name = "date",
              description = "조회할 날짜 (기본값: 오늘)",
              in = ParameterIn.QUERY
          ),
          @Parameter(
              name = "groupBy",
              description = "뚜두 그룹화 방법 [ goal | time ] (기본값: goal)",
              in = ParameterIn.QUERY
          )
      }
  )
  ResponseEntity<?> getDaily(Long loginId, Long userId, LocalDate date, String groupBy);

  @Operation(summary = "주간 뚜두 조회")
  @ApiResponse(
      responseCode = "200"
  )
  ResponseEntity<List<TodoCompletionResponse>> getWeeklyCompletion(
      Long loginId, Long userId, LocalDate date
  );

  @Operation(summary = "월간 뚜두 조회")
  @ApiResponse(
      responseCode = "200"
  )
  ResponseEntity<List<TodoCompletionResponse>> getMonthlyCompletion(
      Long loginId, Long userId, YearMonth yearMonth
  );

  @Operation(summary = "뚜두 수정")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = DduduInfo.class)
      )
  )
  ResponseEntity<DduduInfo> update(Long loginId, Long id, UpdateTodoRequest request);

  @Operation(summary = "뚜두 상태 변경")
  @ApiResponse(
      responseCode = "204"
  )
  ResponseEntity<Void> updateStatus(Long loginId, Long id);

  @Operation(summary = "뚜두 삭제")
  @ApiResponse(
      responseCode = "204"
  )
  ResponseEntity<Void> delete(Long loginId, Long id);

  @Operation(summary = "뚜두 시작/종료시간 설정")
  @ApiResponse(responseCode = "204")
  ResponseEntity<Void> setUpPeriod(Long loginId, Long id, PeriodSetupRequest request);

  // TODO: 구현 후 설명 수정
  @Operation(
      summary = "뚜두 날짜 변경",
      description = "아직 구현되지 않은 기능입니다."
  )
  @ApiResponse(responseCode = "204")
  ResponseEntity<Void> moveDate(Long loginId, Long id, MoveDateRequest request);

}
