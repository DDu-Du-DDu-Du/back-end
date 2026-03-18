package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.request.CreateTodoRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;

public interface CreateTodoUseCase {

  BasicTodoResponse create(Long loginId, CreateTodoRequest request);

}
