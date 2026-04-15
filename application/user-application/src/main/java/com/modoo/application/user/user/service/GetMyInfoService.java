package com.modoo.application.user.user.service;

import com.modoo.application.common.dto.user.response.MeResponse;
import com.modoo.application.common.port.user.in.GetMyInfoUseCase;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.UserErrorCode;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetMyInfoService implements GetMyInfoUseCase {

  private final UserLoaderPort userLoaderPort;

  @Override
  public MeResponse getMyInfo(Long loginId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName()
    );

    return MeResponse.from(user);
  }

}
