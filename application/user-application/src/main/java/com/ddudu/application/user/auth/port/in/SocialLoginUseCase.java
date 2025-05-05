package com.ddudu.application.user.auth.port.in;

import com.ddudu.application.user.auth.dto.request.SocialRequest;
import com.ddudu.application.user.auth.dto.response.TokenResponse;

public interface SocialLoginUseCase {

  TokenResponse login(SocialRequest request);

}
