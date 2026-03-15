package com.ddudu.domain.planning.ddudu.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateDduduCommand(
    Long goalId,
    String name,
    LocalDate scheduledOn,
    LocalTime beginAt,
    LocalTime endAt,
    Integer remindDays,
    Integer remindHours,
    Integer remindMinutes
) {

}
