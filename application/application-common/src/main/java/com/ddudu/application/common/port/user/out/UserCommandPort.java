package com.ddudu.application.common.port.user.out;

import com.ddudu.domain.user.user.aggregate.User;

public interface UserCommandPort {

  User save(User user);

  User update(User user);

}
