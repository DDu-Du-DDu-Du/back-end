package com.ddudu.application.user.auth.port.in;

import com.ddudu.application.user.auth.dto.request.TokenRefreshRequest;
import com.ddudu.application.user.auth.dto.response.TokenResponse;

public interface TokenRefreshUseCase {

  TokenResponse refresh(TokenRefreshRequest request);

}
