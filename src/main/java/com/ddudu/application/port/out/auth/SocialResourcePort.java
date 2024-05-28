package com.ddudu.application.port.out.auth;

import com.ddudu.application.domain.user.domain.vo.AuthProvider;
import com.ddudu.application.dto.authentication.request.SocialRequest;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
