package com.modoo.application.common.port.auth.out;

import com.modoo.domain.user.user.aggregate.User;

public interface SignUpPort {

  User save(User user);

}
