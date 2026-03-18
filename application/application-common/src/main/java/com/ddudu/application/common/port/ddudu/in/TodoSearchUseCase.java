package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.SimpleTodoSearchDto;
import com.ddudu.application.common.dto.ddudu.request.TodoSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface TodoSearchUseCase {

  ScrollResponse<SimpleTodoSearchDto> search(Long loginId, TodoSearchRequest request);

}
