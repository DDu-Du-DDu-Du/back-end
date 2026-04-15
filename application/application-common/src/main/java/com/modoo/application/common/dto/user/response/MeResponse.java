package com.modoo.application.common.dto.user.response;

import com.modoo.common.dto.Authority;
import com.modoo.domain.user.user.aggregate.User;
import lombok.Builder;

@Builder
public record MeResponse(
    Long id,
    String username,
    String nickname,
    String profileImageUrl,
    Authority authority
) {

  public static MeResponse from(User user) {
    return MeResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .nickname(user.getNickname())
        .profileImageUrl(user.getProfileImageUrl())
        .authority(user.getAuthority())
        .build();
  }

}
