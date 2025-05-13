package com.ddudu.application.common.dto.periodgoal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "기간 목표 수정 요청")
public record UpdatePeriodGoalRequest(
    @Schema(
        name = "contents",
        description = "긴간 목표 내용",
        example = "- 1일 1컵 물 마시기"
    )
    @NotNull(message = "4001 CONTENTS_NOT_EXISTING")
    String contents
) {

}
