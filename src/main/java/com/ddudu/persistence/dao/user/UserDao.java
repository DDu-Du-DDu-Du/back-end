package com.ddudu.persistence.dao.user;

import com.ddudu.persistence.entity.UserEntity;
import com.ddudu.user.domain.Email;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<UserEntity, Long>, UserDaoCustom {

  boolean existsByEmail(Email email);

  boolean existsByOptionalUsername(String existsByOptionalUsername);

  Optional<UserEntity> findByEmail(Email email);

}
