package com.modoo.application.common.port.todo.out;

import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.application.common.dto.todo.TodoCursorDto;
import java.util.List;

public interface TodoSearchPort {

  List<TodoCursorDto> search(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine
  );

}
