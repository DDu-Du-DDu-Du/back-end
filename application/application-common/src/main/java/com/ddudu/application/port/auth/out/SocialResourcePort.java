package com.ddudu.application.port.auth.out;

import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.application.dto.auth.request.SocialRequest;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
