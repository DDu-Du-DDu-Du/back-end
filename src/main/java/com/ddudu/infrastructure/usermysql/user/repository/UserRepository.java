package com.ddudu.infrastructure.usermysql.user.repository;

import com.ddudu.infrastructure.usermysql.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserQueryRepository {

}
