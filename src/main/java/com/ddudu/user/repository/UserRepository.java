package com.ddudu.user.repository;

import com.ddudu.persistence.entity.UserEntity;
import com.ddudu.user.domain.Email;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {

  boolean existsByEmail(Email email);

  boolean existsByOptionalUsername(String existsByOptionalUsername);

  Optional<UserEntity> findByEmail(Email email);

}
