package com.ddudu.application.common.port.auth.in;

import com.ddudu.application.common.dto.auth.request.TokenRefreshRequest;
import com.ddudu.application.common.dto.auth.response.TokenResponse;

public interface TokenRefreshUseCase {

  TokenResponse refresh(TokenRefreshRequest request);

}
