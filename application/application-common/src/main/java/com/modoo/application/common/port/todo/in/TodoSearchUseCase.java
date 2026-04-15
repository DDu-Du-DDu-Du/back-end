package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.scroll.response.ScrollResponse;
import com.modoo.application.common.dto.todo.SimpleTodoSearchDto;
import com.modoo.application.common.dto.todo.request.TodoSearchRequest;

public interface TodoSearchUseCase {

  ScrollResponse<SimpleTodoSearchDto> search(Long loginId, TodoSearchRequest request);

}
