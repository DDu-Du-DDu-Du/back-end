package com.ddudu.application.stats.service;

import static java.util.Objects.isNull;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.application.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.port.stats.in.CalculateCompletionUseCase;
import com.ddudu.application.port.stats.out.DduduStatsPort;
import com.ddudu.application.port.user.out.UserLoaderPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.repeatddudu.util.DayOfWeekUtil;
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

  private final UserLoaderPort userLoaderPort;
  private final DduduStatsPort dduduStatsPort;

  @Override
  public List<DduduCompletionResponse> calculateWeekly(Long loginId, Long userId, LocalDate date) {
    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(date);
    LocalDate afterOneWeek = firstDayOfWeek.plusDays(7);

    return calculate(loginId, userId, firstDayOfWeek, afterOneWeek);
  }

  @Override
  public List<DduduCompletionResponse> calculateMonthly(
      Long loginId,
      Long userId,
      YearMonth yearMonth
  ) {
    LocalDate firstDayOfMonth = isNull(yearMonth) ? YearMonth.now()
        .atDay(1) : yearMonth.atDay(1);
    LocalDate afterOneMonth = firstDayOfMonth.plusMonths(1);

    return calculate(loginId, userId, firstDayOfMonth, afterOneMonth);
  }

  private List<DduduCompletionResponse> calculate(
      Long loginId,
      Long userId,
      LocalDate from,
      LocalDate to
  ) {
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = loginUser;

    if (Objects.nonNull(userId)) {
      user = userLoaderPort.getUserOrElseThrow(
          userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());
    }

    return generateCompletions(from, to, loginUser, user);
  }

  private List<DduduCompletionResponse> generateCompletions(
      LocalDate startDate, LocalDate endDate, User loginUser, User user
  ) {
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    Map<LocalDate, DduduCompletionResponse> completionByDate = dduduStatsPort.calculateDdudusCompletion(
            startDate, endDate, user.getId(), accessiblePrivacyTypes)
        .stream()
        .collect(Collectors.toMap(DduduCompletionResponse::date, response -> response));

    List<DduduCompletionResponse> completionList = new ArrayList<>();
    for (LocalDate currentDate = startDate; currentDate.isBefore(endDate);
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
