package com.ddudu.application.common.dto.user.response;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.common.dto.Authority;
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
