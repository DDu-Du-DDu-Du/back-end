package com.ddudu.application.user.user.service;

import com.ddudu.application.common.dto.user.request.UpdateUserSettingsRequest;
import com.ddudu.application.common.dto.user.response.UserSettingsResponse;
import com.ddudu.application.common.port.user.in.UpdateUserSettingsUseCase;
import com.ddudu.application.common.port.user.out.UserCommandPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateUserSettingsService implements UpdateUserSettingsUseCase {

  private final UserLoaderPort userLoaderPort;
  private final UserCommandPort userCommandPort;

  @Override
  public UserSettingsResponse update(Long loginId, UpdateUserSettingsRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName()
    );
    Options options = request.toOptions(
        user.isAllowingFollowsAfterApproval(),
        user.isNotifyingTemplate(),
        user.isNotifyingDdudu()
    );
    User updated = user.updateOption(options);

    return UserSettingsResponse.from(userCommandPort.save(updated));
  }

}
