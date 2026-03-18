package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.request.ChangeNameRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;

public interface ChangeNameUseCase {

  BasicTodoResponse change(Long loginId, Long todoId, ChangeNameRequest request);

}
