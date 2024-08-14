package com.ddudu.application.dto.repeat_ddudu.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "반복 뚜두 수정 요청")
public record UpdateRepeatDduduRequest(
    @Schema(
        name = "name",
        description = "반복 뚜두명",
        example = "물 한 컵 마시기"
    )
    @NotNull(message = "6001 BLANK_NAME")
    @Size(max = 50, message = "6006 EXCESSIVE_NAME_LENGTH")
    String name,
    @Schema(
        name = "repeatType",
        description = "반복 유형 (소문자 가능)",
        example = "DAILY | WEEKLY | MONTHLY"
    )
    @NotBlank(message = "6003 NULL_REPEAT_TYPE")
    String repeatType,
    @Schema(
        name = "repeatDaysOfWeek",
        description = "반복 요일 (WEEKLY일 때만)",
        nullable = true,
        example = "[\"월\", \"화\"]"
    )
    List<String> repeatDaysOfWeek,
    @Schema(
        name = "repeatDaysOfMonth",
        description = "반복 날짜 (MONTHLY 때만)",
        nullable = true,
        example = "[1, 15]"
    )
    List<Integer> repeatDaysOfMonth,
    @Schema(
        name = "lastDayOfMonth",
        description = "마지막 날 반복 여부 (MONTHLY 때만)",
        nullable = true,
        example = "true"
    )
    Boolean lastDayOfMonth,
    @Schema(
        name = "startDate",
        description = "반복 시작 날짜",
        example = "2024-06-10"
    )
    @NotNull(message = "6004 NULL_START_DATE")
    LocalDate startDate,
    @Schema(
        name = "endDate",
        description = "반복 종료 날짜",
        example = "2024-12-31"
    )
    @NotNull(message = "6005 NULL_END_DATE")
    LocalDate endDate,
    @Schema(
        name = "beginAt",
        description = "시작 시간",
        nullable = true,
        example = "07:00:00"
    )
    LocalTime beginAt,
    @Schema(
        name = "endAt",
        description = "종료 시간",
        nullable = true,
        example = "08:00:00"
    )
    LocalTime endAt
) {

}
