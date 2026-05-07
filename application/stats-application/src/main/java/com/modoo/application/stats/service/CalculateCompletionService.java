package com.modoo.application.stats.service;

import com.modoo.application.common.dto.stats.response.TodoCompletionResponse;
import com.modoo.application.common.port.stats.in.CalculateCompletionUseCase;
import com.modoo.application.common.port.stats.out.TodoStatsPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.StatsErrorCode;
import com.modoo.common.time.DateTimeRange;
import com.modoo.common.time.TimeZoneConverter;
import com.modoo.common.util.DayOfWeekUtil;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.domain.user.user.aggregate.enums.Relationship;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculateCompletionService implements CalculateCompletionUseCase {

  private static final boolean IS_ACHIEVED = true;

  private final UserLoaderPort userLoaderPort;
  private final TodoStatsPort todoStatsPort;

  @Deprecated
  public List<TodoCompletionResponse> calculateWeekly(Long loginId, Long userId, LocalDate date) {
    return calculateWeekly(loginId, userId, date, null);
  }

  @Deprecated
  @Override
  public List<TodoCompletionResponse> calculateWeekly(
      Long loginId, Long userId, LocalDate date, String timeZone
  ) {
    ZoneId clientZone = TimeZoneConverter.parseOrUtc(timeZone);
    LocalDate targetDate = Objects.requireNonNullElse(date, LocalDate.now(clientZone));
    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(targetDate);
    LocalDate afterOneWeek = firstDayOfWeek.plusDays(6);
    DateTimeRange range = toUtcRange(firstDayOfWeek, afterOneWeek, clientZone);

    return calculate(loginId, userId, range.startDate(), range.endDate());
  }

  public List<TodoCompletionResponse> calculateMonthly(
      Long loginId, Long userId, YearMonth yearMonth
  ) {
    return calculateMonthly(loginId, userId, yearMonth, null);
  }

  @Override
  public List<TodoCompletionResponse> calculateMonthly(
      Long loginId,
      Long userId,
      YearMonth yearMonth,
      String timeZone
  ) {
    ZoneId clientZone = TimeZoneConverter.parseOrUtc(timeZone);
    YearMonth month = Objects.requireNonNullElse(yearMonth, YearMonth.now(clientZone));
    LocalDate firstDayOfMonth = month.minusMonths(1)
        .atDay(1);
    LocalDate endDate = month.plusMonths(1)
        .atEndOfMonth();
    DateTimeRange range = toUtcRange(firstDayOfMonth, endDate, clientZone);

    return calculate(loginId, userId, range.startDate(), range.endDate());
  }

  private DateTimeRange toUtcRange(LocalDate startDate, LocalDate endDate, ZoneId clientZone) {
    return new DateTimeRange(
        startDate.atTime(LocalTime.MIN)
            .atZone(clientZone)
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toLocalDateTime(),
        endDate.atTime(LocalTime.MAX)
            .atZone(clientZone)
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toLocalDateTime()
    );
  }

  private List<TodoCompletionResponse> calculate(
      Long loginId,
      Long userId,
      LocalDate from,
      LocalDate to
  ) {
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId,
        StatsErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Long targetUserId = Objects.requireNonNullElse(userId, loginId);
    User user = userLoaderPort.getUserOrElseThrow(
        targetUserId,
        StatsErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    return generateCompletions(from, to, loginUser, user);
  }

  private List<TodoCompletionResponse> generateCompletions(
      LocalDate startDate,
      LocalDate endDate,
      User loginUser,
      User user
  ) {
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    return todoStatsPort.calculateTodosCompletion(
            startDate,
            endDate,
            user.getId(),
            null,
            accessiblePrivacyTypes,
            IS_ACHIEVED
        )
        .stream()
        .filter(response -> response.totalCount() > 0)
        .toList();
  }

}
