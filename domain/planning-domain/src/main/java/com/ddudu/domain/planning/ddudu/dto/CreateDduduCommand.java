package com.ddudu.domain.planning.ddudu.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateDduduCommand(
    Long goalId,
    String name,
    LocalDate scheduledOn
) {

}
