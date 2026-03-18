package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.CreateTodoRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;

public interface CreateTodoUseCase {

  BasicTodoResponse create(Long loginId, CreateTodoRequest request);

}
