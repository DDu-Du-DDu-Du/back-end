package com.ddudu.application.port.in.user;

import com.ddudu.application.dto.user.MeResponse;

public interface GetMyInfoUseCase {

  MeResponse getMyInfo(Long loginId);

}
