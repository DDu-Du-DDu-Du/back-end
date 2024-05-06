package com.ddudu.infrastructure.persistence.repository.user;

import com.ddudu.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserQueryRepository {

  boolean existsByUsername(String existsByOptionalUsername);

}
