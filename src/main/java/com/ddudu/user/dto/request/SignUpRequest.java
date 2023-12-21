package com.ddudu.user.dto.request;

public record SignUpRequest(
    String optionalUsername,
    String email,
    String password, String nickname
) {

}
