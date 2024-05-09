package com.ddudu.application.port.out;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import java.util.Optional;

public interface UserLoaderPort {

  Optional<User> loadSocialUser(AuthProvider authProvider);

}
