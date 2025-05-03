package com.ddudu.infrastructure.usermysql.auth.repository;

import com.ddudu.infrastructure.usermysql.auth.entiy.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String>,
    RefreshTokenQueryRepository {

}
