package com.modoo.application.common.port.user.out;

import com.modoo.domain.user.user.aggregate.User;

public interface UserCommandPort {

  User save(User user);

  User update(User user);

}
