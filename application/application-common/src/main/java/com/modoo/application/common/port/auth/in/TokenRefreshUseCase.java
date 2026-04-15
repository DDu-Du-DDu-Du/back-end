package com.modoo.application.common.port.auth.in;

import com.modoo.application.common.dto.auth.request.TokenRefreshRequest;
import com.modoo.application.common.dto.auth.response.TokenResponse;

public interface TokenRefreshUseCase {

  TokenResponse refresh(TokenRefreshRequest request);

}
