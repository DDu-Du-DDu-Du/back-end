package com.ddudu.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
    String optionalUsername,
    @NotBlank(message = "이메일이 입력되지 않았습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    String password,
    @NotBlank(message = "닉네임이 입력되지 않았습니다.")
    @Size(max = 20, message = "닉네임은 최대 20자 입니다.")
    String nickname
) {

}
