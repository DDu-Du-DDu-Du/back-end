package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.scroll.response.ScrollResponse;
import com.modoo.application.common.dto.todo.SimpleTodoSearchDto;
import com.modoo.application.common.dto.todo.TodoCursorDto;
import com.modoo.application.common.dto.todo.request.TodoSearchRequest;
import com.modoo.application.common.port.todo.in.TodoSearchUseCase;
import com.modoo.application.common.port.todo.out.TodoSearchPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.user.user.aggregate.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoSearchService implements TodoSearchUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TodoSearchPort todoSearchPort;

  @Override
  public ScrollResponse<SimpleTodoSearchDto> search(Long loginId, TodoSearchRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.USER_NOT_EXISTING.getCodeName()
    );
    List<TodoCursorDto> todosWithCursor = todoSearchPort.search(
        user.getId(),
        request.getScroll(),
        request.getQuery(),
        request.getIsMine()
    );

    return getScrollResponse(todosWithCursor, request.getSize());
  }

  private ScrollResponse<SimpleTodoSearchDto> getScrollResponse(
      List<TodoCursorDto> todosWithCursor,
      int size
  ) {
    List<SimpleTodoSearchDto> simpleTodos = todosWithCursor.stream()
        .limit(size)
        .map(TodoCursorDto::todo)
        .toList();
    String nextCursor = getNextCursor(todosWithCursor, size);

    return ScrollResponse.from(simpleTodos, nextCursor);
  }

  private String getNextCursor(List<TodoCursorDto> todosWithCursor, int size) {
    if (todosWithCursor.size() > size) {
      return todosWithCursor.get(size - 1)
          .cursor();
    }

    return null;
  }

}
