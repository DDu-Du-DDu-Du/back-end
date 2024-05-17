package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.port.out.auth.TokenLoaderPort;
import com.ddudu.application.port.out.auth.TokenManipulationPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.RefreshTokenEntity;
import com.ddudu.infrastructure.persistence.repository.token.RefreshTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class TokenPersistenceAdpater implements TokenManipulationPort, TokenLoaderPort {

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
