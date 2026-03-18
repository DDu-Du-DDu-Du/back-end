package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.request.MoveDateRequest;

public interface MoveDateUseCase {

  void moveDate(Long loginId, Long todoId, MoveDateRequest request);

}
