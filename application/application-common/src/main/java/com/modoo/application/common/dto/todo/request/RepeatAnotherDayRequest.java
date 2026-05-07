package com.modoo.application.common.dto.todo.request;

import java.time.LocalDate;

public record RepeatAnotherDayRequest(LocalDate repeatOn, String timeZone) {

}
