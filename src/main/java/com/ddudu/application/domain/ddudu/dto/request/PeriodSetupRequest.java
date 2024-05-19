package com.ddudu.application.domain.ddudu.dto.request;

import java.time.LocalDateTime;

public record PeriodSetupRequest(LocalDateTime beginAt, LocalDateTime endAt) {

}
