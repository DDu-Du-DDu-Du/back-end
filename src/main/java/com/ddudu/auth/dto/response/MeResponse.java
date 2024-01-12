package com.ddudu.auth.dto.response;

import com.ddudu.user.domain.User;
import lombok.Builder;

@Builder
public record MeResponse(Long id, String email, String optionalUsername, String nickname) {

  public static MeResponse from(User user) {
    return MeResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .optionalUsername(user.getOptionalUsername())
        .build();
  }

}
