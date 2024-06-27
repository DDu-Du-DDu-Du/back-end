package com.ddudu.application.port.out.user;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import java.util.Optional;

public interface UserLoaderPort {

  User getUserOrElseThrow(Long id, String message);

  Optional<User> loadSocialUser(AuthProvider authProvider);

  Optional<User> loadFullUser(Long userId);

  Optional<User> loadMinimalUser(Long id);

}
