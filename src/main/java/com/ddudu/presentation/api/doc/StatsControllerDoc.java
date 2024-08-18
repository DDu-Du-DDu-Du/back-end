package com.ddudu.presentation.api.doc;

import com.ddudu.application.dto.goal.response.MonthlyStatsSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.YearMonth;
import org.springframework.http.ResponseEntity;

@Tag(
    name = "Stats",
    description = "스탯 관련 API"
)
public interface StatsControllerDoc {

  @Operation(summary = "월별 달성 뚜두 수 통계")
  @ApiResponse(
      responseCode = "200"
  )
  ResponseEntity<MonthlyStatsSummaryResponse> collectSummary(
      Long loginId, YearMonth yearMonth
  );

}
