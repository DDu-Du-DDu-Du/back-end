package com.ddudu.domain.planning.goal.dto;

import lombok.Builder;

@Builder
public record CreateGoalCommand(
    String name,
    String color,
    String privacyType
) {

}
