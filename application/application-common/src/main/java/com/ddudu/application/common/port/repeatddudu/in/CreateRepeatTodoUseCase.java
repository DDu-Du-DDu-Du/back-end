package com.ddudu.application.common.port.repeattodo.in;

import com.ddudu.application.common.dto.repeattodo.request.CreateRepeatTodoRequest;

public interface CreateRepeatTodoUseCase {

  Long create(Long loginId, CreateRepeatTodoRequest request);

}
