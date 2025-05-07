package com.ddudu.application.port.auth.out;

import com.ddudu.domain.user.user.aggregate.User;

public interface SignUpPort {

  User save(User user);

}
