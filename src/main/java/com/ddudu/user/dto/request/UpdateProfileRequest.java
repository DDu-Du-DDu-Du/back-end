package com.ddudu.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank(message = "닉네임이 입력되지 않았습니다.")
    @Size(max = 20, message = "닉네임은 최대 20자 입니다.")
    String nickname,
    @Size(max = 50, message = "자기소개는 최대 50자 입니다.")
    String introduction
) {

}
