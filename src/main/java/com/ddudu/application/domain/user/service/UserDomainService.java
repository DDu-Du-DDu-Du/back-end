package com.ddudu.application.domain.user.service;

import com.ddudu.application.annotation.DomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.enums.RandomUserAdjective;
import com.ddudu.application.domain.user.domain.enums.RandomUserAnimal;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import java.util.Collections;
import java.util.UUID;

@DomainService
public class UserDomainService {

  public User createFirstUser(AuthProvider authProvider) {
    String lowTime = UUID.randomUUID()
        .toString()
        .substring(0, 8);
    RandomUserAdjective adjective = RandomUserAdjective.getRandom();
    RandomUserAnimal animal = RandomUserAnimal.getRandom();
    String username = adjective.getUsername() + animal.getUsername() + lowTime;
    String nickname = adjective.getNickname() + " " + animal.getNickname();

    return User.builder()
        .username(username)
        .nickname(nickname)
        .authProviders(Collections.singletonList(authProvider))
        .build();
  }

}
