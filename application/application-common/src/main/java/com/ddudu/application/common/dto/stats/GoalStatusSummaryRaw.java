package com.ddudu.application.common.dto.stats;

import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;

public record GoalStatusSummaryRaw(
    Long dduduId,
    TodoStatus status
) {

}

