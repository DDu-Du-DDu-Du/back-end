package com.ddudu.application.stats.service;

import com.ddudu.application.common.dto.stats.response.TodoCompletionResponse;
import com.ddudu.application.common.port.stats.in.CalculateCompletionUseCase;
import com.ddudu.application.common.port.stats.out.TodoStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.common.util.DayOfWeekUtil;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.Relationship;
import java.time.LocalDate;
import java.time.YearMonth;
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
  private final TodoStatsPort dduduStatsPort;

  @Deprecated
  @Override
  public List<TodoCompletionResponse> calculateWeekly(Long loginId, Long userId, LocalDate date) {
    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(date);
    LocalDate afterOneWeek = firstDayOfWeek.plusDays(6);

    return calculate(loginId, userId, firstDayOfWeek, afterOneWeek);
  }

  @Override
  public List<TodoCompletionResponse> calculateMonthly(
      Long loginId,
      Long userId,
      YearMonth yearMonth
  ) {
    YearMonth month = Objects.requireNonNullElse(yearMonth, YearMonth.now());
    LocalDate firstDayOfMonth = month.minusMonths(1)
        .atDay(1);
    LocalDate endDate = month.plusMonths(1)
        .atEndOfMonth();

    return calculate(loginId, userId, firstDayOfMonth, endDate);
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

    return dduduStatsPort.calculateTodosCompletion(
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
