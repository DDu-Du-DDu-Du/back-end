package com.ddudu.application.stats.dto;

import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import java.time.LocalDate;

public record StatsBaseDto(
    Long id,
    Long goalId,
    DduduStatus status,
    boolean isPostponed,
    LocalDate scheduledOn
) {

}
