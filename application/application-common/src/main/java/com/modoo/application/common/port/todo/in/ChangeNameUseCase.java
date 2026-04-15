package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.request.ChangeNameRequest;
import com.modoo.application.common.dto.todo.response.BasicTodoResponse;

public interface ChangeNameUseCase {

  BasicTodoResponse change(Long loginId, Long todoId, ChangeNameRequest request);

}
