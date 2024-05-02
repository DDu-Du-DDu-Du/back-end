package com.ddudu.application.user.dto.response;

import com.ddudu.application.user.domain.User;
import lombok.Builder;

@Builder
public record SignUpResponse(Long id, String email, String nickname) {

  public static SignUpResponse from(User user) {
    return SignUpResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .build();
  }

}
