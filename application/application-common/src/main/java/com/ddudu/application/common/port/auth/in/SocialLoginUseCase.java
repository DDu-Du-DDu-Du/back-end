package com.ddudu.application.common.port.auth.in;

import com.ddudu.application.common.dto.auth.request.SocialRequest;
import com.ddudu.application.common.dto.auth.response.TokenResponse;

public interface SocialLoginUseCase {

  TokenResponse login(SocialRequest request);

}
