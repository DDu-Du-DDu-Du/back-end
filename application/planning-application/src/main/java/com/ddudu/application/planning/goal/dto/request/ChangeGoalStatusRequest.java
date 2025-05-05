package com.ddudu.application.planning.goal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "목표 상태 변경 요청")
public record ChangeGoalStatusRequest(
    @Schema(
        name = "status",
        description = "목표 상태",
        example = "IN_PROGRESS | DONE"
    )
    @NotNull(message = "3005 NULL_STATUS")
    String status
) {

}
