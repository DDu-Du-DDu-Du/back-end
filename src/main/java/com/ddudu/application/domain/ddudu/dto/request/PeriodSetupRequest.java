package com.ddudu.application.domain.ddudu.dto.request;

import java.time.LocalTime;

public record PeriodSetupRequest(LocalTime beginAt, LocalTime endAt) {

}
