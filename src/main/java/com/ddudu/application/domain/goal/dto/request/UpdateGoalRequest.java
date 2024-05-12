package com.ddudu.application.domain.goal.dto.request;

import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateGoalRequest(
    @NotBlank(message = "3001 BLANK_NAME")
    @Size(max = 50, message = "3002 EXCESSIVE_NAME_LENGTH")
    String name,
    @NotNull(message = "3005 NULL_STATUS")
    GoalStatus status,
    @NotBlank(message = "3006 BLANK_COLOR")
    @Size(max = 6, message = "3003 INVALID_COLOR_FORMAT")
    String color,
    @NotNull(message = "3007 NULL_PRIVACY_TYPE")
    PrivacyType privacyType
) {

}
