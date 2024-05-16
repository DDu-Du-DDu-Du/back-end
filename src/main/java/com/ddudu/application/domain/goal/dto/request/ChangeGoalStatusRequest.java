package com.ddudu.application.domain.goal.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeGoalStatusRequest(
    @NotNull(message = "3005 NULL_STATUS")
    String status
) {

}
