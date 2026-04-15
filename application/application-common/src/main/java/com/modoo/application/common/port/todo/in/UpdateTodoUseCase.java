package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.request.UpdateTodoRequest;
import com.modoo.application.common.dto.todo.response.BasicTodoResponse;

public interface UpdateTodoUseCase {

  BasicTodoResponse update(Long loginId, Long todoId, UpdateTodoRequest request);

}
