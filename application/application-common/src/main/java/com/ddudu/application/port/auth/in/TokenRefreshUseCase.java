package com.ddudu.application.port.auth.in;

import com.ddudu.application.dto.auth.request.TokenRefreshRequest;
import com.ddudu.application.dto.auth.response.TokenResponse;

public interface TokenRefreshUseCase {

  TokenResponse refresh(TokenRefreshRequest request);

}
