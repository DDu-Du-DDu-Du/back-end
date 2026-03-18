package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;

public interface UpdateTodoUseCase {

  BasicTodoResponse update(Long loginId, Long dduduId, UpdateTodoRequest request);

}
