package com.ddudu.application.common.dto.todo.response;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;

@Schema(description = "뚜두 상세 응답")
@Builder
public record TodoDetailResponse(
    @Schema(
        description = "뚜두 ID",
        example = "1"
    )
    Long id,
    @Schema(
        description = "뚜두 이름",
        example = "로그인 PR 날리기"
    )
    String name,
    @Schema(
        description = "뚜두 메모",
        example = "리뷰 코멘트 반영 후 머지"
    )
    String memo,
    @Schema(
        description = "뚜두 상태 [UNCOMPLETED|COMPLETE]",
        example = "UNCOMPLETED"
    )
    TodoStatus status,
    @Schema(
        description = "목표 ID",
        example = "1"
    )
    Long goalId,
    @Schema(
        description = "반복 뚜두 ID",
        example = "1"
    )
    Long repeatTodoId,
    @Schema(
        type = "string",
        pattern = "yyyy-MM-dd",
        example = "2024-07-08"
    )
    LocalDate scheduledOn,
    @Schema(
        type = "string",
        pattern = "HH:mm",
        example = "14:00"
    )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "HH:mm"
    )
    LocalTime beginAt,
    @Schema(
        type = "string",
        pattern = "HH:mm",
        example = "15:00"
    )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "HH:mm"
    )
    LocalTime endAt,
    LocalDateTime postponedAt,
    LocalDateTime remindAt
) {

  public static TodoDetailResponse from(Todo todo) {
    return TodoDetailResponse.builder()
        .id(todo.getId())
        .beginAt(todo.getBeginAt())
        .endAt(todo.getEndAt())
        .goalId(todo.getGoalId())
        .memo(todo.getMemo())
        .name(todo.getName())
        .postponedAt(todo.getPostponedAt())
        .remindAt(todo.getRemindAt())
        .repeatTodoId(todo.getRepeatTodoId())
        .scheduledOn(todo.getScheduledOn())
        .status(todo.getStatus())
        .build();
  }

}
