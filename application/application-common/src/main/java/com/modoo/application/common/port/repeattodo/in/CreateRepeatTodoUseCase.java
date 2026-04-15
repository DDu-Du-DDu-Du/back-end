package com.modoo.application.common.port.repeattodo.in;

import com.modoo.application.common.dto.repeattodo.request.CreateRepeatTodoRequest;

public interface CreateRepeatTodoUseCase {

  Long create(Long loginId, CreateRepeatTodoRequest request);

}
