package com.ddudu.application.common.dto.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SimpleTodoSearchDto(
    Long id,
    String name,
    LocalDate scheduledOn,
    LocalDateTime postponedAt
) {

}
