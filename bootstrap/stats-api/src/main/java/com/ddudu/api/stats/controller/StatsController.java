package com.ddudu.api.stats.controller;

import static java.util.Objects.isNull;

import com.ddudu.api.stats.doc.StatsControllerDoc;
import com.ddudu.application.common.dto.stats.CompletionPerGoalDto;
import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.stats.in.CalculateCompletionUseCase;
import com.ddudu.application.common.port.stats.in.CollectMonthlyCreationStatsUseCase;
import com.ddudu.application.common.port.stats.in.CollectMonthlyStatsSummaryUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController implements StatsControllerDoc {

  private final CollectMonthlyStatsSummaryUseCase collectMonthlyStatsSummaryUseCase;
  private final CollectMonthlyCreationStatsUseCase collectMonthlyCreationStatsUseCase;
  private final CalculateCompletionUseCase calculateCompletionUseCase;

  /**
   * 월별 뚜두 완료율 조회 API (달성 뚜두 수 / 생성 뚜두 수)
   */
  @GetMapping("/monthly")
  public ResponseEntity<List<DduduCompletionResponse>> getMonthlyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(
          value = "date",
          required = false
      )
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    List<DduduCompletionResponse> response = calculateCompletionUseCase.calculateMonthly(
        loginId,
        userId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

  /**
   * 주간 뚜두 완료율 조회 API
   */
  @GetMapping("/completion/weekly")
  public ResponseEntity<List<DduduCompletionResponse>> getWeeklyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date
  ) {
    userId = isNull(userId) ? loginId : userId;

    List<DduduCompletionResponse> response = calculateCompletionUseCase.calculateWeekly(
        loginId,
        userId,
        date
    );

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<MonthlyStatsSummaryResponse> collectSummary(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryUseCase.collectMonthlyTotalStats(
        loginId, yearMonth);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/completion")
  public ResponseEntity<GenericStatsResponse<CompletionPerGoalDto>> collectCreation(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    GenericStatsResponse<CompletionPerGoalDto> response = collectMonthlyCreationStatsUseCase.collectCreation(
        loginId, yearMonth);

    return ResponseEntity.ok(response);
  }

}
