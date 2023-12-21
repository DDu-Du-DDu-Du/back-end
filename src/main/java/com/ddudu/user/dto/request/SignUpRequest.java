package com.ddudu.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    String optionalUsername,
    @NotBlank(message = "이메일이 입력되지 않았습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    String password,
    @NotBlank(message = "닉네임이 입력되지 않았습니다.")
    @Max(20)
    String nickname
) {

}
