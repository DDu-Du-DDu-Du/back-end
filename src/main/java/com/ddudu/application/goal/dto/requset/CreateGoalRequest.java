package com.ddudu.application.goal.dto.requset;

import com.ddudu.application.goal.domain.PrivacyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGoalRequest(
    @NotBlank(message = "목표가 입력되지 않았습니다.")
    @Size(max = 50, message = "목표는 최대 50자 입니다.")
    String name,
    @Size(max = 6, message = "색상 코드는 6자리 16진수입니다.")
    String color,
    PrivacyType privacyType
) {

}
