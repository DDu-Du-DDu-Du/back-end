package com.ddudu.application.port.out;

import com.ddudu.application.domain.authentication.domain.RefreshToken;
import java.util.List;

public interface TokenManipulationPort {

  Integer getNextFamilyOfUser(Long userId);

  void save(RefreshToken refreshToken);

  void deleteAllFamily(List<RefreshToken> tokenFamily);

}
