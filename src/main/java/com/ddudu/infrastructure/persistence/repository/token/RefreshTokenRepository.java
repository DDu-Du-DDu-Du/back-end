package com.ddudu.infrastructure.persistence.repository.token;

import com.ddudu.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String>,
    RefreshTokenQueryRepository {

}
