package com.ddudu.application.common.port.user.in;

import com.ddudu.application.common.dto.user.response.MeResponse;

public interface GetMyInfoUseCase {

  MeResponse getMyInfo(Long loginId);

}
