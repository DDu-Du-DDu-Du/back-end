package com.ddudu.old.user.dto;

import com.ddudu.application.domain.user.domain.User;
import lombok.Builder;

@Builder
public record SimpleUserDto(Long id, String nickname) {

  public static SimpleUserDto from(User user) {
    return SimpleUserDto.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .build();
  }

}
