package com.ddudu.application.common.dto.repeattodo;

import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeattodo.aggregate.vo.RepeatPattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "반복 투두 요약")
public record RepeatTodoDto(
    @Schema(
        description = "반복 투두 식별자",
        example = "1"
    )
    Long id,
    @Schema(
        description = "반복 투두 이름",
        example = "매일 아침 물 마시기"
    )
    String name,
    @Schema(
        description = "반복 유형 [DAILY|WEEKLY|MONTHLY]",
        example = "DAILY"
    )
    RepeatType repeatType,
    @Schema(description = "반복 투두 반복 패턴")
    RepeatPattern repeatPattern,
    @Schema(
        description = "반복 투두 시작 날짜",
        example = "2024-01-01"
    )
    LocalDate startDate,
    @Schema(
        description = "반복 투두 종료 날짜",
        example = "2024-12-31"
    )
    LocalDate endDate,
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
        example = "14:30"
    )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "HH:mm"
    )
    LocalTime endAt
) {

  public static RepeatTodoDto from(RepeatTodo repeatTodo) {
    return new RepeatTodoDto(
        repeatTodo.getId(),
        repeatTodo.getName(),
        repeatTodo.getRepeatType(),
        repeatTodo.getRepeatPattern(),
        repeatTodo.getStartDate(),
        repeatTodo.getEndDate(),
        repeatTodo.getBeginAt(),
        repeatTodo.getEndAt()
    );
  }

}
