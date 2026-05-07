package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.response.TodoDetailResponse;

public interface RetrieveTodoUseCase {

  TodoDetailResponse findById(Long loginId, Long id, String timeZone);

}
