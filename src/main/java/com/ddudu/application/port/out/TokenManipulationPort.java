package com.ddudu.application.port.out;

import com.ddudu.application.domain.authentication.domain.RefreshToken;

public interface TokenManipulationPort {

  Integer getNextFamilyOfUser(Long userId);

  RefreshToken save(RefreshToken refreshToken);

}
