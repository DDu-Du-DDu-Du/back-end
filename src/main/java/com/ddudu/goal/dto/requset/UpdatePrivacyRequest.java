package com.ddudu.goal.dto.requset;

import com.ddudu.goal.domain.PrivacyType;
import jakarta.validation.constraints.NotNull;

public record UpdatePrivacyRequest(
    @NotNull(message = "공개 설정이 입력되지 않았습니다.")
    PrivacyType privacyType
) {

}