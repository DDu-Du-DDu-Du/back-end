package com.ddudu.infra.mysql.user.auth.repository;

import com.ddudu.infra.mysql.user.auth.entiy.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String>,
    RefreshTokenQueryRepository {

}
