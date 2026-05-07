package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.GoalGroupedTodos;
import com.modoo.application.common.dto.todo.TimeGroupedTodos;
import com.modoo.application.common.dto.todo.response.TimetableResponse;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.todo.in.GetTimetableUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.application.planning.todo.model.Timetable;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.common.time.DateTimeRange;
import com.modoo.common.time.TimeZoneConverter;
import com.modoo.domain.planning.goal.aggregate.Goal;
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
public class GetTimetableService implements GetTimetableUseCase {

  private final TodoLoaderPort todoLoaderPort;
  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;

  public TimetableResponse get(Long loginId, Long userId, LocalDate date) {
    return get(loginId, userId, date, null);
  }

  @Override
  public TimetableResponse get(Long loginId, Long userId, LocalDate date, String timeZone) {
    // 1. 요청 유저, 검색 대상 유저 조회 및 검증
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId,
        TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    User user = userLoaderPort.getUserOrElseThrow(
        userId,
        TodoErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    // 2. 두 유저 사이의 관계 확인 및 접근 가능한 PrivacyType 조회
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    // 3. 타임 테이블 조회
    ZoneId clientZone = TimeZoneConverter.parseOrUtc(timeZone);
    LocalDate targetDate = Objects.requireNonNullElse(date, LocalDate.now(clientZone));
    DateTimeRange range = TimeZoneConverter.toUtcDateRange(targetDate, clientZone);
    List<Todo> todos = todoLoaderPort.getTodosBetween(
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
        .toList();
    Timetable timetable = new Timetable(todos);

    // 4. 응답 생성 (데이터 변환)
    List<Goal> goals = goalLoaderPort.findAllByUserAndPrivacyTypes(
        user.getId(),
        accessiblePrivacyTypes
    );
    List<TimeGroupedTodos> assignedTodos = timetable.getTimeGroupedTodos(goals);
    List<GoalGroupedTodos> unassignedTodos = timetable.getUnassignedTodosWithGoal(goals);

    return TimetableResponse.of(assignedTodos, unassignedTodos);
  }

}
