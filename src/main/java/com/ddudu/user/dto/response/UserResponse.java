package com.ddudu.user.dto.response;

import com.ddudu.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(Long id, String email, String optionalUsername, String nickname) {

  public static UserResponse from(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .optionalUsername(user.getOptionalUsername())
        .build();
  }
}
