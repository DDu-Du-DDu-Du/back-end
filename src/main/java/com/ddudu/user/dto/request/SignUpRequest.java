package com.ddudu.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
    @Size(max = 20, message = "아이디는 최대 20자 입니다.")
    String optionalUsername,
    @NotBlank(message = "이메일이 입력되지 않았습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&^]).{8,50}$",
        message = "비밀번호는 영문, 숫자, 특수문자로 구성되어야 합니다."
    )
    String password,
    @NotBlank(message = "닉네임이 입력되지 않았습니다.")
    @Size(max = 20, message = "닉네임은 최대 20자 입니다.")
    String nickname,
    @Size(max = 50, message = "자기소개는 최대 50자 입니다.")
    String introduction
) {

}
