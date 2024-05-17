package com.ddudu.application.port.in.user;

import com.ddudu.application.domain.user.dto.MeResponse;

public interface GetMyInfoUseCase {

  MeResponse getMyInfo(Long loginId);

}
