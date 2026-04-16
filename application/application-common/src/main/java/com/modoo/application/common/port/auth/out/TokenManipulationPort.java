package com.modoo.application.common.port.auth.out;

import com.modoo.domain.user.auth.aggregate.RefreshToken;
import java.time.LocalDateTime;
import java.util.List;

public interface TokenManipulationPort {

  Integer getNextFamilyOfUser(Long userId);

  void save(RefreshToken refreshToken);

  void deleteAllFamily(List<RefreshToken> tokenFamily);

  void deleteByUserFamily(Long userId, int family);

  long rotateIfCurrentMatches(
      Long userId,
      int family,
      String currentToken,
      String newCurrentToken,
      LocalDateTime refreshedAt
  );

}
