package com.ddudu.infrastructure.usermysql.auth.repository;

import com.ddudu.infrastructure.usermysql.auth.entiy.RefreshTokenEntity;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenQueryRepository {

  Optional<RefreshTokenEntity> getLastFamilyByUserId(Long userId);

  List<RefreshTokenEntity> findAllByUserFamily(Long userId, int family);

}
