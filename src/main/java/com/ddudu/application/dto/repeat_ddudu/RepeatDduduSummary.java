package com.ddudu.application.dto.repeat_ddudu;

import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "반복 뚜두 요약")
public record RepeatDduduSummary(
    @Schema(description = "반복 뚜두 식별자", example = "1")
    Long id,
    @Schema(description = "반복 뚜두 이름", example = "매일 아침 물 마시기")
    String name,
    @Schema(description = "반복 뚜두 반복 패턴")
    RepeatPattern repeatPattern,
    @Schema(description = "반복 뚜두 시작 날짜", example = "2024-01-01")
    LocalDate startDate,
    @Schema(description = "반복 뚜두 종료 날짜", example = "2024-12-31")
    LocalDate endDate
) {

  public static RepeatDduduSummary from(RepeatDdudu repeatDdudu) {
    return new RepeatDduduSummary(
        repeatDdudu.getId(),
        repeatDdudu.getName(),
        repeatDdudu.getRepeatPattern(),
        repeatDdudu.getStartDate(),
        repeatDdudu.getEndDate()
    );
  }

}
