package com.ddudu.application.dto.period_goal.response;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import lombok.Builder;

@Builder
@Schema(description = "기간 목표 요약 정보")
public record PeriodGoalSummary(
    @Schema(
        name = "id",
        description = "기간 목표 식별자",
        example = "1"
    )
    Long id,
    @Schema(
        name = "contents",
        description = "긴간 목표 내용",
        example = "- 1일 1컵 물 마시기"
    )
    String contents,
    @Schema(
        name = "type",
        description = "기간 목표 타입",
        example = "WEEK"
    )
    PeriodGoalType type
) {

  public static PeriodGoalSummary ofNullable(Optional<PeriodGoal> optionalPeriodGoal) {
    if (optionalPeriodGoal.isEmpty()) {
      return empty();
    }

    return PeriodGoalSummary.from(optionalPeriodGoal.get());
  }

  public static PeriodGoalSummary from(PeriodGoal periodGoal) {
    return PeriodGoalSummary.builder()
        .id(periodGoal.getId())
        .contents(periodGoal.getContents())
        .type(periodGoal.getType())
        .build();
  }

  public static PeriodGoalSummary empty() {
    return PeriodGoalSummary.builder()
        .build();
  }

}
