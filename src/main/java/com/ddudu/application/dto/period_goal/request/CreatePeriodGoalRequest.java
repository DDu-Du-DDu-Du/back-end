package com.ddudu.application.dto.period_goal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "기간 목표 생성 요청")
public record CreatePeriodGoalRequest(
    @Schema(
        name = "contents",
        description = "긴간 목표 내용",
        example = "- 1일 1컵 물 마시기"
    )
    @NotBlank(message = "4001 CONTENTS_NOT_EXISTING")
    String contents,
    @Schema(
        name = "type",
        description = "기간 목표 타입",
        example = "WEEK | MONTH"
    )
    @NotBlank(message = "4002 PERIOD_GOAL_TYPE_NOT_EXISTING")
    String type,
    @Schema(
        name = "planDate",
        description = "기간 목표 날짜 (type이 WEEK일 때는 해당 날짜가 있는 주의 화요일, MONTH일 때는 해당 날짜가 있는 달의 1일이 저장됩니다.)",
        example = "2024-06-10"
    )
    @NotNull(message = "4003 PLAN_DATE_NOT_EXISTING")
    LocalDate planDate
) {

}
