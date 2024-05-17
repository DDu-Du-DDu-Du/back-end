package com.ddudu.application.domain.user.dto;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.enums.Authority;
import lombok.Builder;

@Builder
public record MeResponse(
    Long id, String username, String nickname, String profileImageUrl, Authority authority
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
