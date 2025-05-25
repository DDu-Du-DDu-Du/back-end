package com.ddudu.api.stats.controller;

import static java.util.Objects.isNull;

import com.ddudu.api.stats.doc.StatsControllerDoc;
import com.ddudu.application.common.dto.stats.AchievementPerGoal;
import com.ddudu.application.common.dto.stats.CreationCountPerGoalDto;
import com.ddudu.application.common.dto.stats.PostponedPerGoal;
import com.ddudu.application.common.dto.stats.SustenancePerGoal;
import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.stats.in.CalculateCompletionUseCase;
import com.ddudu.application.common.port.stats.in.CollectMonthlyAchievementUseCase;
import com.ddudu.application.common.port.stats.in.CollectMonthlyCreationStatsUseCase;
import com.ddudu.application.common.port.stats.in.CollectMonthlyPostponementUseCase;
import com.ddudu.application.common.port.stats.in.CollectMonthlyStatsSummaryUseCase;
import com.ddudu.application.common.port.stats.in.CollectMonthlySustenanceUseCase;
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
  private final CollectMonthlyAchievementUseCase collectMonthlyAchievementUseCase;
  private final CollectMonthlySustenanceUseCase collectMonthlySustenanceUseCase;
  private final CollectMonthlyPostponementUseCase collectMonthlyPostponementUseCase;

  /**
   * 월별 뚜두 완료율 조회 API (달성 뚜두 수 / 생성 뚜두 수)
   */
  @GetMapping("/completion/monthly")
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
        loginId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

  @GetMapping("/creation")
  public ResponseEntity<GenericStatsResponse<CreationCountPerGoalDto>> collectCreation(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    GenericStatsResponse<CreationCountPerGoalDto> response = collectMonthlyCreationStatsUseCase.collectCreation(
        loginId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/achievement")
  public ResponseEntity<GenericStatsResponse<AchievementPerGoal>> collectAchievement(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    GenericStatsResponse<AchievementPerGoal> response = collectMonthlyAchievementUseCase.collectAchievement(
        loginId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/sustenance")
  public ResponseEntity<GenericStatsResponse<SustenancePerGoal>> collectSustenanceCount(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    GenericStatsResponse<SustenancePerGoal> response = collectMonthlySustenanceUseCase.collectSustenanceCount(
        loginId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/postponement")
  public ResponseEntity<GenericStatsResponse<PostponedPerGoal>> collectPostponedCount(
      @Login
      Long loginId,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM")
      YearMonth yearMonth
  ) {
    GenericStatsResponse<PostponedPerGoal> response = collectMonthlyPostponementUseCase.collectPostponement(
        loginId,
        yearMonth
    );

    return ResponseEntity.ok(response);
  }

}
