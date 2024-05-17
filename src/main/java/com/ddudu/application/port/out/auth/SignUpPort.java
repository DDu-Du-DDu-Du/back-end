package com.ddudu.application.port.out.auth;

import com.ddudu.application.domain.user.domain.User;

public interface SignUpPort {

  User save(User user);

}
