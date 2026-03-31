package com.ddudu.application.common.port.reminder.in;

public interface CancelReminderByIdUseCase {

  void cancel(Long loginId, Long reminderId);

}
