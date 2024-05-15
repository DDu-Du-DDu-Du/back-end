package com.ddudu.application.port.out;

import com.ddudu.application.domain.authentication.domain.RefreshToken;
import java.util.List;

public interface TokenLoaderPort {

  List<RefreshToken> loadByUserFamily(Long userId, int family);

}
