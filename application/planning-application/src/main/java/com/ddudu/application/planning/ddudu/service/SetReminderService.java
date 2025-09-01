package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.SetReminderRequest;
import com.ddudu.application.common.port.ddudu.in.SetReminderUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DduduUpdatePort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SetReminderService implements SetReminderUseCase {

  private final UserLoaderPort userLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public void setReminder(Long loginId, Long id, SetReminderRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        id,
        DduduErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateDduduCreator(user.getId());

    Ddudu dduduWithReminder = ddudu.setReminder(request.days(), request.hours(), request.minutes());

    dduduUpdatePort.update(dduduWithReminder);

    if (ddudu.isScheduledToday()) {
      // TODO: TaskScheduler 구현 후 Scheduler에 추가하기
    }
  }

}
