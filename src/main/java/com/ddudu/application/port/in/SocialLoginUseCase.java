package com.ddudu.application.port.in;

import com.ddudu.application.domain.user.dto.request.SocialRequest;
import com.ddudu.application.domain.user.dto.response.TokenResponse;

public interface SocialLoginUseCase {

  TokenResponse login(SocialRequest request);

}
