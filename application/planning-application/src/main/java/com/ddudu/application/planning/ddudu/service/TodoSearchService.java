package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.TodoCursorDto;
import com.ddudu.application.common.dto.ddudu.SimpleTodoSearchDto;
import com.ddudu.application.common.dto.ddudu.request.TodoSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.ddudu.in.TodoSearchUseCase;
import com.ddudu.application.common.port.ddudu.out.TodoSearchPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoSearchService implements TodoSearchUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TodoSearchPort dduduSearchPort;

  @Override
  public ScrollResponse<SimpleTodoSearchDto> search(Long loginId, TodoSearchRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.USER_NOT_EXISTING.getCodeName()
    );
    List<TodoCursorDto> ddudusWithCursor = dduduSearchPort.search(
        user.getId(),
        request.getScroll(),
        request.getQuery(),
        request.getIsMine()
    );

    return getScrollResponse(ddudusWithCursor, request.getSize());
  }

  private ScrollResponse<SimpleTodoSearchDto> getScrollResponse(
      List<TodoCursorDto> ddudusWithCursor,
      int size
  ) {
    List<SimpleTodoSearchDto> simpleTodos = ddudusWithCursor.stream()
        .limit(size)
        .map(TodoCursorDto::ddudu)
        .toList();
    String nextCursor = getNextCursor(ddudusWithCursor, size);

    return ScrollResponse.from(simpleTodos, nextCursor);
  }

  private String getNextCursor(List<TodoCursorDto> ddudusWithCursor, int size) {
    if (ddudusWithCursor.size() > size) {
      return ddudusWithCursor.get(size - 1)
          .cursor();
    }

    return null;
  }

}
