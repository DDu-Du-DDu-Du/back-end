package com.ddudu.application.user.auth.port.out;

import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.application.user.auth.dto.request.SocialRequest;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
