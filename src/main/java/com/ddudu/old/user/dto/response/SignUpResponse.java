package com.ddudu.old.user.dto.response;

import com.ddudu.application.domain.user.domain.User;
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
