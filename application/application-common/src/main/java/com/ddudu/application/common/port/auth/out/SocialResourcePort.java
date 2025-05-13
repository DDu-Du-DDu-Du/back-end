package com.ddudu.application.common.port.auth.out;

import com.ddudu.application.common.dto.auth.request.SocialRequest;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
