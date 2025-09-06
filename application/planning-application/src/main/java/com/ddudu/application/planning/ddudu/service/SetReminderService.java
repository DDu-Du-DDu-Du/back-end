package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.SetReminderRequest;
import com.ddudu.application.common.port.ddudu.in.SetReminderUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DduduUpdatePort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
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
  private final NotificationEventLoaderPort notificationEventLoaderPort;
  private final NotificationEventCommandPort notificationEventCommandPort;

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
    upsertNotification(user.getId(), ddudu);
  }

  private void upsertNotification(Long userId, Ddudu ddudu) {
    boolean notificationRegistered = notificationEventLoaderPort.existsByContext(
        NotificationEventTypeCode.DDUDU,
        ddudu.getId()
    );

    if (notificationRegistered) {
      // TODO: TaskScheduler 구현 후 Scheduler에서 삭제
    }

    NotificationEvent notificationEvent = NotificationEvent.builder()
        .contextId(ddudu.getId())
        .typeCode(NotificationEventTypeCode.DDUDU)
        .receiverId(userId)
        .senderId(userId)
        .willFireAt(ddudu.getRemindAt())
        .build();

    notificationEventCommandPort.save(notificationEvent);

    if (ddudu.isScheduledToday()) {
      // TODO: TaskScheduler 구현 후 Scheduler에 추가하기
    }
  }

}
