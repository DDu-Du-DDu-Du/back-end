package com.modoo.application.common.port.auth.out;

import com.modoo.domain.user.auth.aggregate.RefreshToken;
import java.util.List;
import java.util.Optional;

public interface TokenLoaderPort {

  List<RefreshToken> loadByUserFamily(Long userId, int family);

  Optional<RefreshToken> loadOneByUserFamily(Long userId, int family);

}
