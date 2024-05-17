package com.ddudu.application.port.in.auth;

import com.ddudu.application.domain.authentication.dto.request.SocialRequest;
import com.ddudu.application.domain.authentication.dto.response.TokenResponse;

public interface SocialLoginUseCase {

  TokenResponse login(SocialRequest request);

}
