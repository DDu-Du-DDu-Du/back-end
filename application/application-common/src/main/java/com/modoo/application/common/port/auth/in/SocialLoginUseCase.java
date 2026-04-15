package com.modoo.application.common.port.auth.in;

import com.modoo.application.common.dto.auth.request.SocialRequest;
import com.modoo.application.common.dto.auth.response.TokenResponse;

public interface SocialLoginUseCase {

  TokenResponse login(SocialRequest request);

}
