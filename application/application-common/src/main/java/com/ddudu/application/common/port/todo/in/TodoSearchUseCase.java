package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.dto.todo.SimpleTodoSearchDto;
import com.ddudu.application.common.dto.todo.request.TodoSearchRequest;

public interface TodoSearchUseCase {

  ScrollResponse<SimpleTodoSearchDto> search(Long loginId, TodoSearchRequest request);

}
