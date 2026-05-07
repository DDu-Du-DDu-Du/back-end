package com.modoo.api.stats.controller;

import com.modoo.api.stats.doc.StatsControllerDoc;
import com.modoo.application.common.dto.stats.response.AchievedStatsDetailResponse;
import com.modoo.application.common.dto.stats.response.GoalDetailStatsSummaryResponse;
import com.modoo.application.common.dto.stats.response.MonthlyStatsReportResponse;
import com.modoo.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.modoo.application.common.dto.stats.response.PostponedStatsDetailResponse;
import com.modoo.application.common.dto.stats.response.TodoCompletionResponse;
import com.modoo.application.common.port.stats.in.CalculateCompletionUseCase;
import com.modoo.application.common.port.stats.in.CollectGoalDetailStatsUseCase;
import com.modoo.application.common.port.stats.in.CollectMonthlyStatsDetailUseCase;
import com.modoo.application.common.port.stats.in.CollectMonthlyStatsReportUseCase;
import com.modoo.application.common.port.stats.in.CollectMonthlyStatsSummaryUseCase;
import com.modoo.bootstrap.common.annotation.Login;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController implements StatsControllerDoc {

  private final CollectMonthlyStatsReportUseCase collectMonthlyStatsReportUseCase;
  private final CalculateCompletionUseCase calculateCompletionUseCase;
  private final CollectMonthlyStatsSummaryUseCase collectMonthlyStatsSummaryUseCase;
  private final CollectMonthlyStatsDetailUseCase collectMonthlyStatsDetailUseCase;
  private final CollectGoalDetailStatsUseCase collectGoalDetailStatsUseCase;

  /**
   * 월별 투두 완료율 조회 API (달성 투두 수 / 생성 투두 수)
   */
  @GetMapping("/completion/monthly")
  public ResponseEntity<List<TodoCompletionResponse>> getMonthlyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(
          value = "yearMonth",
          required = false
      )
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth,
      @RequestParam(required = false)
      String timeZone
  ) {
    List<TodoCompletionResponse> response = calculateCompletionUseCase.calculateMonthly(
        loginId,
        userId,
        yearMonth,
        timeZone
    );

    return ResponseEntity.ok(response);
  }

  /**
   * 주간 투두 완료율 조회 API
   */
  @Deprecated
  @GetMapping("/completion/weekly")
  public ResponseEntity<List<TodoCompletionResponse>> getWeeklyCompletion(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate date,
      @RequestParam(required = false)
      String timeZone
  ) {
    List<TodoCompletionResponse> response = calculateCompletionUseCase.calculateWeekly(
        loginId,
        userId,
        date,
        timeZone
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/report")
  public ResponseEntity<MonthlyStatsReportResponse> collectReport(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    MonthlyStatsReportResponse response = collectMonthlyStatsReportUseCase.collectReport(
        loginId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/summary")
  public ResponseEntity<MonthlyStatsSummaryResponse> collectSummary(
      @Login
      Long loginId,
      @RequestParam(required = false)
      Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryUseCase.collectSummary(
        loginId,
        userId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/detail/{goalId}/achieved")
  public ResponseEntity<AchievedStatsDetailResponse> collectAchievedDetail(
      @Login
      Long loginId,
      @PathVariable("goalId")
      Long goalId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth fromMonth,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth toMonth
  ) {
    AchievedStatsDetailResponse response = collectMonthlyStatsDetailUseCase.collectAchievedDetail(
        loginId,
        goalId,
        fromMonth,
        toMonth
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/detail/{goalId}/postponed")
  public ResponseEntity<PostponedStatsDetailResponse> collectPostponedDetail(
      @Login
      Long loginId,
      @PathVariable("goalId")
      Long goalId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth fromMonth,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth toMonth
  ) {
    PostponedStatsDetailResponse response = collectMonthlyStatsDetailUseCase.collectPostponedDetail(
        loginId,
        goalId,
        fromMonth,
        toMonth
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/detail/{goalId}")
  public ResponseEntity<GoalDetailStatsSummaryResponse> collectGoalDetailStats(
      @Login
      Long loginId,
      @PathVariable("goalId")
      Long goalId,
      @RequestParam(required = false)
      Long userId
  ) {
    GoalDetailStatsSummaryResponse response = collectGoalDetailStatsUseCase.collectDetail(
        loginId,
        goalId,
        userId
    );

    return ResponseEntity.ok(response);
  }


}
