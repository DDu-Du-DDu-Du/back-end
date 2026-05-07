package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.GoalGroupedTodos;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.todo.in.GetDailyTodosByGoalUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.application.planning.todo.model.TodoList;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.common.time.DateTimeRange;
import com.modoo.common.time.TimeZoneConverter;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.domain.user.user.aggregate.enums.Relationship;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDailyTodosByGoalService implements GetDailyTodosByGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public List<GoalGroupedTodos> get(Long loginId, Long userId, LocalDate date, String timeZone) {
    // 1. 요청 사용자와 조회 대상 사용자 조회
    User loginUser = getUser(loginId, TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = getUser(userId, TodoErrorCode.USER_NOT_EXISTING.getCodeName());

    // 2. 사용자 간 관계 확인
    List<PrivacyType> accessiblePrivacyTypes = getAccessiblePrivacyTypes(loginUser, user);

    // 3. 투두 조회
    ZoneId clientZone = TimeZoneConverter.parseOrUtc(timeZone);
    LocalDate targetDate = getTargetDate(date, clientZone);
    List<Todo> convertedTodos = getTodos(user, accessiblePrivacyTypes, targetDate, clientZone);
    TodoList todos = new TodoList(convertedTodos);

    return todos.getTodosWithGoal(goalLoaderPort.findAllByUserAndPrivacyTypes(
        user.getId(),
        accessiblePrivacyTypes
    ));
  }

  private User getUser(Long userId, String errorCode) {
    return userLoaderPort.getUserOrElseThrow(userId, errorCode);
  }

  private List<PrivacyType> getAccessiblePrivacyTypes(User loginUser, User user) {
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    return PrivacyType.getAccessibleTypesIn(relationship);
  }

  private LocalDate getTargetDate(LocalDate date, ZoneId clientZone) {
    return Objects.requireNonNullElse(date, LocalDate.now(clientZone));
  }

  private List<Todo> getTodos(
      User user,
      List<PrivacyType> accessiblePrivacyTypes,
      LocalDate targetDate,
      ZoneId clientZone
  ) {
    DateTimeRange range = TimeZoneConverter.toUtcDateRange(targetDate, clientZone);

    return todoLoaderPort.getTodosBetween(
            range.start(),
            range.end(),
            user.getId(),
            accessiblePrivacyTypes
        )
        .stream()
        .filter(todo -> isIncludedInTargetDate(todo, targetDate, range))
        .map(todo -> todo.convert(clientZone))
        .toList();
  }

  private boolean isIncludedInTargetDate(Todo todo, LocalDate targetDate, DateTimeRange range) {
    if (Objects.isNull(todo.getBeginAt())) {
      return todo.getScheduledOn().isEqual(targetDate);
    }

    return TimeZoneConverter.isInRange(todo.getScheduledOn(), todo.getBeginAt(), range);
  }

}
