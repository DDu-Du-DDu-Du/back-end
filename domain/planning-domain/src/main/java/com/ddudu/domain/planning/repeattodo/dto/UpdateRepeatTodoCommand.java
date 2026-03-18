package com.ddudu.domain.planning.repeattodo.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateRepeatTodoCommand(
    String name,
    String repeatType,
    List<String> repeatDaysOfWeek,
    List<Integer> repeatDaysOfMonth,
    Boolean lastDayOfMonth,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime beginAt,
    LocalTime endAt
) {

}
