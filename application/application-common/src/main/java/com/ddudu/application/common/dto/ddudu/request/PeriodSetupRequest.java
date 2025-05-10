package com.ddudu.application.common.dto.ddudu.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record PeriodSetupRequest(
    @Schema(
        name = "beginAt",
        description = "시작시간",
        type = "string",
        format = "time",
        example = "00:00:00"
    )
    LocalTime beginAt,
    @Schema(
        name = "endAt",
        description = "종료시간",
        type = "string",
        format = "time",
        example = "00:00:00"
    )
    LocalTime endAt
) {

}
