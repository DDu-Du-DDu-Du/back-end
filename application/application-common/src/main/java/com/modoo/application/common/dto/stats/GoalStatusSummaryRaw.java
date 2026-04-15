package com.modoo.application.common.dto.stats;

import com.modoo.domain.planning.todo.aggregate.enums.TodoStatus;

public record GoalStatusSummaryRaw(
    Long todoId,
    TodoStatus status
) {

}

