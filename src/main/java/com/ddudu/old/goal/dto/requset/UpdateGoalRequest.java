package com.ddudu.old.goal.dto.requset;

import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateGoalRequest(
    @NotBlank(message = "목표가 입력되지 않았습니다.")
    @Size(max = 50, message = "목표는 최대 50자 입니다.")
    String name,
    @NotNull(message = "목표 상태가 입력되지 않았습니다.")
    GoalStatus status,
    @NotBlank(message = "색상이 입력되지 않았습니다.")
    @Size(max = 6, message = "색상 코드는 6자리 16진수입니다.")
    String color,
    @NotNull(message = "공개 설정이 입력되지 않았습니다.")
    PrivacyType privacyType
) {

}
