package com.ddudu.application.user.auth.port.out;

import com.ddudu.domain.user.user.aggregate.User;

public interface SignUpPort {

  User save(User user);

}
