package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.response.TodoDetailResponse;

public interface RetrieveTodoUseCase {

  TodoDetailResponse findById(Long loginId, Long id);

}
