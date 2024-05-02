package com.ddudu.application.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordRequest(
    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&^]).{8,50}$",
        message = "비밀번호는 8자리 이상의 영문, 숫자, 특수문자로 구성되어야 합니다."
    )
    String password
) {

}
