package com.ddudu.user.dto.response;

import com.ddudu.user.domain.User;

public record SignUpResponse(Long id, String email, String nickname) {

  public static SignUpResponse from(User user) {
    return new SignUpResponse(user.getId(), user.getEmail(), user.getNickname());
  }

}
