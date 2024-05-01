package com.ddudu.application.user.dto.response;

import com.ddudu.application.user.domain.User;
import com.ddudu.application.user.dto.SimpleUserDto;
import java.util.List;
import lombok.Builder;

@Builder
public record UsersResponse(Integer counts, List<SimpleUserDto> users) {

  public static UsersResponse from(List<User> users) {
    List<SimpleUserDto> simpleUsers = users.stream()
        .map(SimpleUserDto::from)
        .toList();

    return UsersResponse.builder()
        .counts(users.size())
        .users(simpleUsers)
        .build();
  }

}
