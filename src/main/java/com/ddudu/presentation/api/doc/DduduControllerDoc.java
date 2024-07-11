package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.ddudu.request.ChangeNameRequest;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.dto.ddudu.request.MoveDateRequest;
import com.ddudu.application.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.dto.ddudu.response.DduduDetailResponse;
import com.ddudu.application.dto.ddudu.response.RepeatAnotherDayResponse;
import com.ddudu.application.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.dto.scroll.response.ScrollResponse;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "뚜두 관련 API")
public interface DduduControllerDoc {

  @Operation(summary = "뚜두 생성")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = BasicDduduResponse.class)
      )
  )
  ResponseEntity<IdResponse> create(Long loginId, CreateDduduRequest request);

  @Operation(summary = "뚜두 상세 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = DduduDetailResponse.class)
      )
  )
  ResponseEntity<DduduDetailResponse> getById(Long loginId, Long id);

  @Operation(summary = "일간 뚜두 리스트 조회 (목표별)")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = GoalGroupedDdudus.class))
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
          )
      }
  )
  ResponseEntity<List<GoalGroupedDdudus>> getDailyList(Long loginId, Long userId, LocalDate date);

  @Operation(summary = "일간 타임테이블 조회 (시간별)")
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
          )
      }
  )
  ResponseEntity<TimetableResponse> getDailyTimetable(Long loginId, Long userId, LocalDate date);


  @Operation(summary = "주간 뚜두 완료도 조회")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = DduduCompletionResponse.class))
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
          )
      }
  )
  ResponseEntity<List<DduduCompletionResponse>> getWeeklyCompletion(
      Long loginId, Long userId, LocalDate date
  );

  @Operation(summary = "월간 뚜두 완료도 조회")
  @ApiResponse(
      responseCode = "200"
  )
  ResponseEntity<List<DduduCompletionResponse>> getMonthlyCompletion(
      Long loginId, Long userId, YearMonth yearMonth
  );

  @Operation(summary = "뚜두 이름(내용) 변경")
  @ApiResponse(
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = IdResponse.class)
      )
  )
  @Parameter(name = "id", description = "변경할 뚜두 식별자", in = ParameterIn.PATH)
  ResponseEntity<IdResponse> changeName(Long loginId, Long id, ChangeNameRequest request);

  @Operation(summary = "뚜두 상태 변경")
  @ApiResponse(
      responseCode = "204"
  )
  @Parameter(name = "id", description = "변경할 뚜두 식별자", in = ParameterIn.PATH)
  ResponseEntity<Void> updateStatus(Long loginId, Long id);

  @Operation(summary = "뚜두 삭제")
  @ApiResponse(
      responseCode = "204"
  )
  @Parameter(name = "id", description = "삭제할 뚜두 식별자", in = ParameterIn.PATH)
  ResponseEntity<Void> delete(Long loginId, Long id);

  @Operation(summary = "뚜두 시작/종료시간 설정")
  @ApiResponse(responseCode = "204")
  ResponseEntity<Void> setUpPeriod(Long loginId, Long id, PeriodSetupRequest request);

  @Operation(summary = "뚜두 날짜 변경")
  @ApiResponse(responseCode = "204")
  ResponseEntity<Void> moveDate(Long loginId, Long id, MoveDateRequest request);

  @Operation(summary = "뚜두 다른 날 반복하기")
  @ApiResponse(
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = RepeatAnotherDayRequest.class)
      )
  )
  ResponseEntity<RepeatAnotherDayResponse> repeatOnAnotherDay(
      Long loginId, Long id, RepeatAnotherDayRequest request
  );

  @Operation(summary = "뚜두 검색. Not Yet Implemented")
  @ApiResponse(responseCode = "200")
  ResponseEntity<ScrollResponse<SimpleDduduSearchDto>> getList(
      Long loginId,
      @ParameterObject
      DduduSearchRequest request
  );

}
