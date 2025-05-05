package com.ddudu.application.user.user.port.out;

import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import java.util.Optional;

public interface UserLoaderPort {

  User getUserOrElseThrow(Long id, String message);

  Optional<User> loadSocialUser(AuthProvider authProvider);

  Optional<User> loadFullUser(Long userId);

}
