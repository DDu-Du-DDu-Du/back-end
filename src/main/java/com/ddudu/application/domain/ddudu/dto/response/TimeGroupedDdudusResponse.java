package com.ddudu.application.domain.ddudu.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;

public record TimeGroupedDdudusResponse(
    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    @Schema(type = "string", pattern = "HH:mm", example = "14:30")
    LocalTime time,
    List<DduduWithColorInfo> ddudus
) {

}
