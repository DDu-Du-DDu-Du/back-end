package com.modoo.infra.mysql.user.user.repository;

import com.modoo.infra.mysql.user.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserQueryRepository {

}
