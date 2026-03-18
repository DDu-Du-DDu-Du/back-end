package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.response.TodoDetailResponse;

public interface RetrieveTodoUseCase {

  TodoDetailResponse findById(Long loginId, Long id);

}
