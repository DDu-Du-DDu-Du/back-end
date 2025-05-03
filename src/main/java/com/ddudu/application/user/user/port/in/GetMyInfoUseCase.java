package com.ddudu.application.user.user.port.in;

import com.ddudu.application.user.user.dto.response.MeResponse;

public interface GetMyInfoUseCase {

  MeResponse getMyInfo(Long loginId);

}
