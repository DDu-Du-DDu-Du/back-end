package com.ddudu.presentation.api.doc;

import com.ddudu.application.domain.ddudu.dto.request.MoveDateRequest;
import com.ddudu.application.domain.ddudu.dto.request.PeriodSetupRequest;
import com.ddudu.application.domain.ddudu.dto.request.RepeatAnotherDayRequest;
import com.ddudu.old.todo.dto.request.CreateTodoRequest;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoInfo;
import com.ddudu.old.todo.dto.response.TodoListResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
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
          schema = @Schema(implementation = TodoInfo.class)
      )
  )
  ResponseEntity<TodoInfo> create(Long loginId, CreateTodoRequest request);

  @Operation(summary = "뚜두 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoResponse.class)
      )
  )
  ResponseEntity<TodoResponse> getById(Long loginId, Long id);

  @Operation(summary = "일간 뚜두 조회")
  @ApiResponse(
      responseCode = "200"
  )
  ResponseEntity<List<TodoListResponse>> getDaily(Long loginId, Long userId, LocalDate date);

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
          schema = @Schema(implementation = TodoInfo.class)
      )
  )
  ResponseEntity<TodoInfo> update(Long loginId, Long id, UpdateTodoRequest request);

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

  // TODO: 구현 후 설명 수정
  @Operation(
      summary = "뚜두 다른 날 반복하기. Not Yet Implemented",
      description = "아직 구현되지 않은 기능입니다."
  )
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = RepeatAnotherDayRequest.class)
      )
  )
  ResponseEntity<Void> repeatOnAnotherDay(Long loginId, Long id, RepeatAnotherDayRequest request);

}
