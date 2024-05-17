package com.ddudu.application.port.out.auth;

import com.ddudu.application.domain.authentication.domain.RefreshToken;
import java.util.List;

public interface TokenLoaderPort {

  List<RefreshToken> loadByUserFamily(Long userId, int family);

}
