package com.ddudu.application.planning.repeatddudu.dto;

import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatPattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "반복 뚜두 요약")
public record RepeatDduduDto(
    @Schema(description = "반복 뚜두 식별자", example = "1")
    Long id,
    @Schema(description = "반복 뚜두 이름", example = "매일 아침 물 마시기")
    String name,
    @Schema(description = "반복 뚜두 반복 패턴")
    RepeatPattern repeatPattern,
    @Schema(description = "반복 뚜두 시작 날짜", example = "2024-01-01")
    LocalDate startDate,
    @Schema(description = "반복 뚜두 종료 날짜", example = "2024-12-31")
    LocalDate endDate,
    @Schema(type = "string", pattern = "HH:mm", example = "14:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime beginAt,
    @Schema(type = "string", pattern = "HH:mm", example = "14:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime endAt
) {

  public static RepeatDduduDto from(RepeatDdudu repeatDdudu) {
    return new RepeatDduduDto(
        repeatDdudu.getId(),
        repeatDdudu.getName(),
        repeatDdudu.getRepeatPattern(),
        repeatDdudu.getStartDate(),
        repeatDdudu.getEndDate(),
        repeatDdudu.getBeginAt(),
        repeatDdudu.getEndAt()
    );
  }

}
