package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.request.MoveDateRequest;

public interface MoveDateUseCase {

  void moveDate(Long loginId, Long todoId, MoveDateRequest request);

}
