package com.ddudu.infrastructure.persistence.repository.user;

import com.ddudu.infrastructure.persistence.dto.FullUser;
import java.util.Optional;

public interface UserQueryRepository {

  Optional<FullUser> fetchFullUserById(Long id);

}
