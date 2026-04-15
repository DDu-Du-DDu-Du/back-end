package com.modoo.application.common.port.repeattodo.in;

import com.modoo.application.common.dto.repeattodo.request.UpdateRepeatTodoRequest;

public interface UpdateRepeatTodoUseCase {

  Long update(Long loginId, Long id, UpdateRepeatTodoRequest request);

}
