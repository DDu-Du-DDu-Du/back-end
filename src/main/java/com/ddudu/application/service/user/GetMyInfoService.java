package com.ddudu.application.service.user;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.exception.UserErrorCode;
import com.ddudu.application.dto.user.MeResponse;
import com.ddudu.application.port.in.user.GetMyInfoUseCase;
import com.ddudu.application.port.out.user.UserLoaderPort;
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
