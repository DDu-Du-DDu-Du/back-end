package com.ddudu.application.domain.goal.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGoalRequest(
    @NotBlank(message = "3001 BLANK_NAME")
    @Size(max = 50, message = "3002 EXCESSIVE_NAME_LENGTH")
    String name,
    @Size(max = 6, message = "3003 INVALID_COLOR_FORMAT")
    String color,
    String privacyType
) {

}
