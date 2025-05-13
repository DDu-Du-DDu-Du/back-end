package com.ddudu.application.common.dto.goal.request;


import com.ddudu.domain.planning.goal.dto.CreateGoalCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateGoalRequest(
    @NotBlank(message = "3001 BLANK_NAME")
    @Size(
        max = 50,
        message = "3002 EXCESSIVE_NAME_LENGTH"
    )
    String name,
    @Size(
        max = 6,
        message = "3003 INVALID_COLOR_FORMAT"
    )
    String color,
    String privacyType,
    List<CreateRepeatDduduRequestWithoutGoal> repeatDdudus
) {

  public CreateGoalCommand toCommand() {
    return CreateGoalCommand.builder()
        .name(name)
        .color(color)
        .privacyType(privacyType)
        .build();
  }

}
