package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.application.common.dto.ddudu.TodoCursorDto;
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
