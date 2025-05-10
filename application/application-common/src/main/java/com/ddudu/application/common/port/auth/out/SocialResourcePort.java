package com.ddudu.application.common.port.auth.out;

import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.application.common.dto.auth.request.SocialRequest;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
