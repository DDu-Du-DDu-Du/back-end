package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.response.TodoDashboardContent;
import com.modoo.application.common.dto.todo.response.TodoDashboardItem;
import com.modoo.application.common.dto.todo.response.TodoDashboardResponse;
import com.modoo.application.common.port.todo.in.GetTodoDashboardUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.common.time.TimeZoneConverter;
import com.modoo.domain.planning.todo.aggregate.Todo;
import java.time.LocalDate;
import java.time.ZoneId;
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

  public TodoDashboardResponse get(Long loginId) {
    return get(loginId, null);
  }

  @Override
  public TodoDashboardResponse get(Long loginId, String timeZone) {
    userLoaderPort.getUserOrElseThrow(loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());

    ZoneId clientZone = TimeZoneConverter.parseOrUtc(timeZone);
    List<Todo> todos = todoLoaderPort.getTodosByUserId(loginId);
    Map<LocalDate, List<Todo>> grouped = new LinkedHashMap<>();

    for (Todo todo : todos) {
      Todo converted = todo.convert(clientZone);
      grouped.computeIfAbsent(converted.getScheduledOn(), key -> new ArrayList<>())
          .add(converted);
    }

    LocalDate today = LocalDate.now(clientZone);
    grouped.computeIfAbsent(today, key -> new ArrayList<>());

    List<TodoDashboardContent> contents = grouped.entrySet().stream()
        .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
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
    return Comparator.comparing(Todo::getStatus)
        .thenComparing(Todo::getBeginAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(Todo::getEndAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(Todo::getId);
  }

}
