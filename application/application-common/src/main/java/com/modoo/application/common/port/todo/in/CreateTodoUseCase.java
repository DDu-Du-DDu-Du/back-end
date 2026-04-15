package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.request.CreateTodoRequest;
import com.modoo.application.common.dto.todo.response.BasicTodoResponse;

public interface CreateTodoUseCase {

  BasicTodoResponse create(Long loginId, CreateTodoRequest request);

}
