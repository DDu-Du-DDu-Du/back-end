package com.ddudu.application.common.port.repeattodo.in;

import com.ddudu.application.common.dto.repeattodo.request.UpdateRepeatTodoRequest;

public interface UpdateRepeatTodoUseCase {

  Long update(Long loginId, Long id, UpdateRepeatTodoRequest request);

}
