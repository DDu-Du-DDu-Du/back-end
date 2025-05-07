package com.ddudu.application.port.auth.in;

import com.ddudu.application.dto.auth.request.SocialRequest;
import com.ddudu.application.dto.auth.response.TokenResponse;

public interface SocialLoginUseCase {

  TokenResponse login(SocialRequest request);

}
