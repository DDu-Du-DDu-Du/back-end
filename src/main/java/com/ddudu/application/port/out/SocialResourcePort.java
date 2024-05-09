package com.ddudu.application.port.out;

import com.ddudu.application.domain.authentication.dto.request.SocialRequest;
import com.ddudu.application.domain.user.domain.vo.AuthProvider;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
