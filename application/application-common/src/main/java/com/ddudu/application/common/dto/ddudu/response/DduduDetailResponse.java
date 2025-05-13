package com.ddudu.application.common.dto.ddudu.response;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "뚜두 상세 응답")
public record DduduDetailResponse(
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
        description = "뚜두 상태 [UNCOMPLETED|COMPLETE]",
        example = "UNCOMPLETED"
    )
    DduduStatus status,
    @Schema(
        description = "목표 ID",
        example = "1"
    )
    Long goalId,
    @Schema(
        description = "반복 뚜두 ID",
        example = "1"
    )
    Long repeatDduduId,
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
    LocalTime endAt

) {

  public static DduduDetailResponse from(Ddudu ddudu) {
    return new DduduDetailResponse(
        ddudu.getId(),
        ddudu.getName(),
        ddudu.getStatus(),
        ddudu.getGoalId(),
        ddudu.getRepeatDduduId(),
        ddudu.getScheduledOn(),
        ddudu.getBeginAt(),
        ddudu.getEndAt()
    );
  }

}
