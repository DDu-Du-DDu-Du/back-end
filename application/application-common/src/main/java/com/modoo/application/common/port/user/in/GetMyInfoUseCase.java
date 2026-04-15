package com.modoo.application.common.port.user.in;

import com.modoo.application.common.dto.user.response.MeResponse;

public interface GetMyInfoUseCase {

  MeResponse getMyInfo(Long loginId);

}
