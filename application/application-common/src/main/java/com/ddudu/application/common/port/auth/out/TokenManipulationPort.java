package com.ddudu.application.common.port.auth.out;

import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import java.util.List;

public interface TokenManipulationPort {

  Integer getNextFamilyOfUser(Long userId);

  void save(RefreshToken refreshToken);

  void deleteAllFamily(List<RefreshToken> tokenFamily);

}
