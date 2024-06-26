package com.ddudu.old.user.dto.response;

import com.ddudu.application.domain.user.domain.User;
import lombok.Builder;

@Builder
public record UserProfileResponse(
    Long id,
    String nickname,
    String introduction
) {

  public static UserProfileResponse from(User user) {
    return UserProfileResponse.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .introduction(user.getIntroduction())
        .build();
  }

}
