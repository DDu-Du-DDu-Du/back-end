package com.ddudu.application.user.auth.port.out;

import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import java.util.List;

public interface TokenLoaderPort {

  List<RefreshToken> loadByUserFamily(Long userId, int family);

}
