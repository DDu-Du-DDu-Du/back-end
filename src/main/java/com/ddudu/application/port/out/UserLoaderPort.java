package com.ddudu.application.port.out;

import com.ddudu.application.domain.user.domain.AuthProvider;
import com.ddudu.application.domain.user.domain.User;
import java.util.Optional;

public interface UserLoaderPort {

  Optional<User> loadSocialUser(AuthProvider authProvider);

}
