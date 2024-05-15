package com.ddudu.infrastructure.persistence.repository.token;

import com.ddudu.infrastructure.persistence.entity.RefreshTokenEntity;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenQueryRepository {

  Optional<RefreshTokenEntity> getLastFamilyByUserId(Long userId);

  List<RefreshTokenEntity> findAllByUserFamily(Long userId, int family);

}
