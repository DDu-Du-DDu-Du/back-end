package com.ddudu.persistence.dao.user;

import com.ddudu.persistence.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<UserEntity, Long>, UserDaoCustom {

  boolean existsByEmail(String email);

  boolean existsByOptionalUsername(String existsByOptionalUsername);

  Optional<UserEntity> findByEmail(String email);

}
