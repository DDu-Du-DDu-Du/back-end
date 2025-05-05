package com.ddudu.infra.mysql.user.auth.adapter;

import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import com.ddudu.application.user.auth.port.out.TokenLoaderPort;
import com.ddudu.application.user.auth.port.out.TokenManipulationPort;
import com.ddudu.application.common.annotation.DrivenAdapter;
import com.ddudu.infra.mysql.user.auth.entiy.RefreshTokenEntity;
import com.ddudu.infra.mysql.user.auth.repository.RefreshTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class AuthPersistenceAdpater implements TokenManipulationPort, TokenLoaderPort {

  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public Integer getNextFamilyOfUser(Long userId) {
    return refreshTokenRepository.getLastFamilyByUserId(userId)
        .map(refreshTokenEntity -> refreshTokenEntity
            .getFamily() + 1)
        .orElse(1);
  }

  @Override
  public void save(RefreshToken refreshToken) {
    refreshTokenRepository.save(RefreshTokenEntity.from(refreshToken));
  }

  @Override
  public void deleteAllFamily(List<RefreshToken> tokenFamily) {
    List<RefreshTokenEntity> tokenEntities = tokenFamily.stream()
        .map(RefreshTokenEntity::from)
        .toList();

    refreshTokenRepository.deleteAll(tokenEntities);
  }

  @Override
  public List<RefreshToken> loadByUserFamily(Long userId, int family) {
    return refreshTokenRepository.findAllByUserFamily(userId, family)
        .stream()
        .map(RefreshTokenEntity::toDomain)
        .toList();
  }

}
