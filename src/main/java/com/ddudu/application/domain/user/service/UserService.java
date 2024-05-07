package com.ddudu.application.domain.user.service;

import com.ddudu.application.domain.user.domain.AuthProvider;
import com.ddudu.application.domain.user.domain.RandomUserAdjective;
import com.ddudu.application.domain.user.domain.RandomUserAnimal;
import com.ddudu.application.domain.user.domain.User;
import java.util.Collections;
import org.springframework.stereotype.Component;

@Component
public class UserService {

  public User create(AuthProvider authProvider) {
    RandomUserAdjective adjective = RandomUserAdjective.getRandom();
    RandomUserAnimal animal = RandomUserAnimal.getRandom();
    String username = adjective.getUsername() + animal.getUsername();
    String nickname = adjective.getNickname() + System.lineSeparator() + animal.getNickname();

    return User.builder()
        .username(username)
        .nickname(nickname)
        .authProviders(Collections.singletonList(authProvider))
        .build();
  }

}
