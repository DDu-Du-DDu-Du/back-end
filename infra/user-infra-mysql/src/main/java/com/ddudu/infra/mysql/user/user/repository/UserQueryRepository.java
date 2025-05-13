package com.ddudu.infra.mysql.user.user.repository;

import com.ddudu.infra.mysql.user.user.dto.FullUser;
import java.util.Optional;

public interface UserQueryRepository {

  Optional<FullUser> fetchFullUserById(Long id);

}
