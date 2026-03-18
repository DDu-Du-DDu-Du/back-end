package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.SimpleTodoSearchDto;
import com.ddudu.application.common.dto.todo.request.TodoSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface TodoSearchUseCase {

  ScrollResponse<SimpleTodoSearchDto> search(Long loginId, TodoSearchRequest request);

}
