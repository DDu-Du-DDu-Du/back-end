package com.modoo.infra.mysql.user.auth.repository;

import com.modoo.infra.mysql.user.auth.entiy.RefreshTokenEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenQueryRepository {

  Optional<RefreshTokenEntity> getLastFamilyByUserId(Long userId);

  List<RefreshTokenEntity> findAllByUserFamily(Long userId, int family);

  Optional<RefreshTokenEntity> findByUserFamily(Long userId, int family);

  void deleteByUserFamily(Long userId, int family);

  long rotateIfCurrentMatches(
      Long userId,
      int family,
      String currentToken,
      String newCurrentToken,
      LocalDateTime refreshedAt
  );

}
