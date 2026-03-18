package com.ddudu.application.common.port.todo.out;

import com.ddudu.application.common.dto.todo.TodoCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import java.util.List;

public interface TodoSearchPort {

  List<TodoCursorDto> search(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine
  );

}
