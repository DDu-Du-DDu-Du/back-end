package com.ddudu.application.port.in.auth;

import com.ddudu.application.dto.authentication.request.SocialRequest;
import com.ddudu.application.dto.authentication.response.TokenResponse;

public interface SocialLoginUseCase {

  TokenResponse login(SocialRequest request);

}
