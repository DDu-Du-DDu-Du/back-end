package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.port.ddudu.in.CancelReminderUseCase;
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
public class CancelReminderService implements CancelReminderUseCase {

  private final UserLoaderPort userLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public void cancel(Long loginId, Long id) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        id,
        DduduErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateDduduCreator(user.getId());

    Ddudu dduduWithoutReminder = ddudu.cancelReminder();
    Ddudu updated = dduduUpdatePort.update(dduduWithoutReminder);

    // TODO: notification event 구현 이후 알림 이벤트 삭제 + TaskScheduler에서 삭제
  }

}
