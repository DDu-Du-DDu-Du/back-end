package com.ddudu.application.common.port.todo.in;

public interface CancelReminderUseCase {

  void cancel(Long loginId, Long id);

}
