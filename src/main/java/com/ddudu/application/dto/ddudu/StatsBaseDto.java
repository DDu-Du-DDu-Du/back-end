package com.ddudu.application.dto.ddudu;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import java.time.LocalDate;

public record StatsBaseDto(
    Long id,
    Long goalId,
    DduduStatus status,
    boolean isPostponed,
    LocalDate scheduledOn
) {

}
