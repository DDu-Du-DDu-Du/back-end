package com.ddudu.application.port.user.in;

import com.ddudu.application.dto.user.response.MeResponse;

public interface GetMyInfoUseCase {

  MeResponse getMyInfo(Long loginId);

}
