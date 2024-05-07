package com.ddudu.application.port.out;

import com.ddudu.application.domain.user.domain.AuthProvider;
import com.ddudu.application.domain.user.dto.request.SocialRequest;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
