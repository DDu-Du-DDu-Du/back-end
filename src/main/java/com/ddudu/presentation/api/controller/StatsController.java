package com.ddudu.presentation.api.controller;

import com.ddudu.application.dto.goal.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.port.in.ddudu.CollectMonthlyStatsSummaryUseCase;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.doc.StatsControllerDoc;
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

  @GetMapping("/stats/numbers")
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

}
