package com.ddudu.application.stats.service;

import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.port.stats.in.CalculateCompletionUseCase;
import com.ddudu.application.common.port.stats.out.DduduStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.common.util.DayOfWeekUtil;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.Relationship;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculateCompletionService implements CalculateCompletionUseCase {

  private static final boolean IS_ACHIEVED = true;

  private final UserLoaderPort userLoaderPort;
  private final DduduStatsPort dduduStatsPort;

  @Override
  public List<DduduCompletionResponse> calculateWeekly(Long loginId, Long userId, LocalDate date) {
    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(date);
    LocalDate afterOneWeek = firstDayOfWeek.plusDays(6);

    return calculate(loginId, userId, firstDayOfWeek, afterOneWeek, true);
  }

  @Override
  public List<DduduCompletionResponse> calculateMonthly(
      Long loginId,
      Long userId,
      YearMonth yearMonth
  ) {
    YearMonth month = Objects.requireNonNullElse(yearMonth, YearMonth.now());
    LocalDate firstDayOfMonth = month.minusMonths(1)
        .atDay(1);
    LocalDate endDate = month.plusMonths(1)
        .atEndOfMonth();

    return calculate(loginId, userId, firstDayOfMonth, endDate, false);
  }

  private List<DduduCompletionResponse> calculate(
      Long loginId,
      Long userId,
      LocalDate from,
      LocalDate to,
      boolean includeEmpty
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

    return generateCompletions(from, to, loginUser, user, includeEmpty);
  }

  private List<DduduCompletionResponse> generateCompletions(
      LocalDate startDate,
      LocalDate endDate,
      User loginUser,
      User user,
      boolean includeEmpty
  ) {
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    Map<LocalDate, DduduCompletionResponse> completionByDate = dduduStatsPort.calculateDdudusCompletion(
            startDate,
            endDate,
            user.getId(),
            null,
            accessiblePrivacyTypes,
            IS_ACHIEVED
        )
        .stream()
        .filter(response -> response.totalCount() > 0)
        .collect(Collectors.toMap(DduduCompletionResponse::date, response -> response));

    if (!includeEmpty) {
      return completionByDate.values()
          .stream()
          .sorted(java.util.Comparator.comparing(DduduCompletionResponse::date))
          .toList();
    }

    List<DduduCompletionResponse> completionList = new ArrayList<>();

    for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate);
        currentDate = currentDate.plusDays(1)) {
      DduduCompletionResponse response = completionByDate.getOrDefault(
          currentDate,
          DduduCompletionResponse.createEmptyResponse(currentDate)
      );

      completionList.add(response);
    }

    return completionList;
  }

}
