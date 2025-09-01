package com.ddudu.application.common.dto.ddudu.request;

import jakarta.validation.constraints.PositiveOrZero;

public record SetReminderRequest(
    @PositiveOrZero(message = "2020 NEGATIVE_REMINDER_INPUT_EXISTS")
    int days,
    @PositiveOrZero(message = "2020 NEGATIVE_REMINDER_INPUT_EXISTS")
    int hours,
    @PositiveOrZero(message = "2020 NEGATIVE_REMINDER_INPUT_EXISTS")
    int minutes
) {

}
