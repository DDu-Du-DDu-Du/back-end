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

  public List<GoalGroupedTodos> get(Long loginId, Long userId, LocalDate date) {
    return get(loginId, userId, date, null);
  }

  @Override
  public List<GoalGroupedTodos> get(Long loginId, Long userId, LocalDate date, String timeZone) {
    // 1. 요청 사용자와 조회 대상 사용자 조회
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    User user = userLoaderPort.getUserOrElseThrow(
        userId,
        TodoErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    // 2. 사용자 간 관계 확인
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    // 3. 투두 조회
    ZoneId clientZone = TimeZoneConverter.parseOrUtc(timeZone);
    LocalDate targetDate = Objects.requireNonNullElse(date, LocalDate.now(clientZone));
    DateTimeRange range = TimeZoneConverter.toUtcDateRange(targetDate, clientZone);
    TodoList todos = new TodoList(todoLoaderPort.getTodosBetween(
            range.start(),
            range.end(),
            user.getId(),
            accessiblePrivacyTypes
        )
        .stream()
        .filter(todo -> Objects.isNull(todo.getBeginAt())
            ? todo.getScheduledOn().isEqual(targetDate)
            : TimeZoneConverter.isInRange(
                todo.getScheduledOn(),
                todo.getBeginAt(),
                range
            ))
        .map(todo -> todo.convert(clientZone))
        .toList());

    return todos.getTodosWithGoal(goalLoaderPort.findAllByUserAndPrivacyTypes(
        user.getId(),
        accessiblePrivacyTypes
    ));
  }

}
