package com.ddudu.old.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "이메일을 찾을 수 없습니다.")
    @Email(message = "이메일을 찾을 수 없습니다.")
    String email,
    @NotBlank(message = "잘못된 비밀번호 입니다.")
    @Size(min = 8, max = 50, message = "잘못된 비밀번호 입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&^]).{8,50}$",
        message = "잘못된 비밀번호 입니다."
    )
    String password) {

}
