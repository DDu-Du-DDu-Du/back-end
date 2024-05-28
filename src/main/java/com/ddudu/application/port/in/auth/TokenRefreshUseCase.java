package com.ddudu.application.port.in.auth;

import com.ddudu.application.dto.authentication.request.TokenRefreshRequest;
import com.ddudu.application.dto.authentication.response.TokenResponse;

public interface TokenRefreshUseCase {

  TokenResponse refresh(TokenRefreshRequest request);

}
