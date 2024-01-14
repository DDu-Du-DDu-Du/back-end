package com.ddudu.user.dto.response;

import com.ddudu.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(Long id, String nickname) {

  public static UserResponse from(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .build();
  }

}
