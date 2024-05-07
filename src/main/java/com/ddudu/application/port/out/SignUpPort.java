package com.ddudu.application.port.out;

import com.ddudu.application.domain.user.domain.User;

public interface SignUpPort {

  User create(User user);

}
