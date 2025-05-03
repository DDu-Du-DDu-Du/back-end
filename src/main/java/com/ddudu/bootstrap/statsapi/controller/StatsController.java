package com.ddudu.bootstrap.statsapi.controller;

import com.ddudu.application.stats.dto.CompletionPerGoalDto;
import com.ddudu.application.stats.dto.response.MonthlyStatsResponse;
import com.ddudu.application.stats.dto.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.stats.port.in.CollectMonthlyCreationStatsUseCase;
import com.ddudu.application.stats.port.in.CollectMonthlyStatsSummaryUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import com.ddudu.bootstrap.statsapi.doc.StatsControllerDoc;
import java.time.YearMonth;
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
  public ResponseEntity<MonthlyStatsResponse<CompletionPerGoalDto>> collectCreation(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    MonthlyStatsResponse<CompletionPerGoalDto> response = collectMonthlyCreationStatsUseCase.collectCreation(
        loginId, yearMonth);

    return ResponseEntity.ok(response);
  }

}
