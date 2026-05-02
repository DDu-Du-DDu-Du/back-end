package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.response.TodoDashboardContent;
import com.modoo.application.common.dto.todo.response.TodoDashboardItem;
import com.modoo.application.common.dto.todo.response.TodoDashboardResponse;
import com.modoo.application.common.port.todo.in.GetTodoDashboardUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.todo.aggregate.Todo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTodoDashboardService implements GetTodoDashboardUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TodoLoaderPort todoLoaderPort;

  @Override
  public TodoDashboardResponse get(Long loginId) {
    userLoaderPort.getUserOrElseThrow(loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());

    List<Todo> todos = todoLoaderPort.getTodosByUserId(loginId);
    Map<LocalDate, List<Todo>> grouped = new LinkedHashMap<>();

    for (Todo todo : todos) {
      grouped.computeIfAbsent(todo.getScheduledOn(), key -> new ArrayList<>()).add(todo);
    }

    LocalDate today = LocalDate.now();
    grouped.computeIfAbsent(today, key -> new ArrayList<>());

    List<TodoDashboardContent> contents = grouped.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(entry -> TodoDashboardContent.of(
            entry.getKey(),
            entry.getValue().stream()
                .sorted(todoComparator())
                .map(TodoDashboardItem::from)
                .toList()
        ))
        .toList();

    int todayIndex = -1;
    for (int i = 0; i < contents.size(); i++) {
      if (contents.get(i).date().isEqual(today)) {
        todayIndex = i;
        break;
      }
    }

    boolean isEmpty = todos.isEmpty();
    return TodoDashboardResponse.of(isEmpty, contents, todayIndex);
  }

  private Comparator<Todo> todoComparator() {
    return Comparator.comparing(Todo::getStatus, Comparator.reverseOrder())
        .thenComparing(Todo::getBeginAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(Todo::getEndAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(Todo::getId);
  }

}
