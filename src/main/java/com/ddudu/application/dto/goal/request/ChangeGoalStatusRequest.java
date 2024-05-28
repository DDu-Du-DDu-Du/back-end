package com.ddudu.application.dto.goal.request;

import jakarta.validation.constraints.NotNull;

public record ChangeGoalStatusRequest(
    @NotNull(message = "3005 NULL_STATUS")
    String status
) {

}
