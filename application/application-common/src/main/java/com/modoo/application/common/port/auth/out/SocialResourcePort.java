package com.modoo.application.common.port.auth.out;

import com.modoo.application.common.dto.auth.request.SocialRequest;
import com.modoo.domain.user.user.aggregate.vo.AuthProvider;

public interface SocialResourcePort {

  AuthProvider retrieveSocialResource(SocialRequest request);

}
