package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;

public interface UpdateTodoUseCase {

  BasicTodoResponse update(Long loginId, Long todoId, UpdateTodoRequest request);

}
