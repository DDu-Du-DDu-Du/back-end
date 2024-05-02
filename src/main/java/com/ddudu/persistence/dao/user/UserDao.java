package com.ddudu.persistence.dao.user;

import com.ddudu.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<UserEntity, Long>, UserDaoCustom {

  boolean existsByUsername(String existsByOptionalUsername);

}
