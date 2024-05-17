package com.ddudu.application.port.in.auth;

import com.ddudu.application.domain.authentication.dto.request.TokenRefreshRequest;
import com.ddudu.application.domain.authentication.dto.response.TokenResponse;

public interface TokenRefreshUseCase {

  TokenResponse refresh(TokenRefreshRequest request);

}
