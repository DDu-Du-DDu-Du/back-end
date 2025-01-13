package com.ddudu.application.dto.goal.response;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.dto.repeat_ddudu.RepeatDduduDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;


@Builder
@Schema(description = "목표 상세 정보")
public record GoalWithRepeatDduduResponse(
    @Schema(description = "목표 식별자", example = "1")
    Long id,
    @Schema(description = "목표 이름", example = "매일 아침 물 마시기")
    String name,
    @Schema(description = "목표 상태 [IN_PROGRESS|DONE]", example = "IN_PROGRESS")
    GoalStatus status,
    @Schema(description = "목표 색상", example = "FFFFFF")
    String color,
    @Schema(description = "목표 공개 범위 [PRIVATE|FOLLOWER|PUBLIC]", example = "PUBLIC")
    PrivacyType privacyType,
    @ArraySchema(schema = @Schema(implementation = RepeatDduduDto.class))
    List<RepeatDduduDto> repeatDdudus
) {

  public static GoalWithRepeatDduduResponse from(Goal goal, List<RepeatDdudu> repeatDdudus) {
    return GoalWithRepeatDduduResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus())
        .color(goal.getColor())
        .privacyType(goal.getPrivacyType())
        .repeatDdudus(
            repeatDdudus.stream()
                .map(RepeatDduduDto::from)
                .toList())
        .build();
  }

}
