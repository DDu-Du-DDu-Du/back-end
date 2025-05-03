package com.ddudu.infrastructure.usermysql.user.repository;

import com.ddudu.infrastructure.usermysql.user.dto.FullUser;
import java.util.Optional;

public interface UserQueryRepository {

  Optional<FullUser> fetchFullUserById(Long id);

}
