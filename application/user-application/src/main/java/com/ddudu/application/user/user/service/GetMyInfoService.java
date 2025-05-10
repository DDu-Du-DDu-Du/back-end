package com.ddudu.application.user.user.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.application.common.dto.user.response.MeResponse;
import com.ddudu.application.common.port.user.in.GetMyInfoUseCase;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetMyInfoService implements GetMyInfoUseCase {

  private final UserLoaderPort userLoaderPort;

  @Override
  public MeResponse getMyInfo(Long loginId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName());

    return MeResponse.from(user);
  }

}
