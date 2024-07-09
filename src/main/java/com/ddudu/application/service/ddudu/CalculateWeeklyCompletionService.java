package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.repeat_ddudu.util.DayOfWeekUtil;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.port.in.ddudu.CalculateWeeklyCompletionUseCase;
import com.ddudu.application.port.out.ddudu.DduduStatsPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculateWeeklyCompletionService implements CalculateWeeklyCompletionUseCase {

  private final UserLoaderPort userLoaderPort;
  private final DduduStatsPort dduduStatsPort;

  @Override
  public List<DduduCompletionResponse> calculate(Long loginId, Long userId, LocalDate date) {
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());
    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(date);
    LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(7);

    return generateCompletions(firstDayOfWeek, lastDayOfWeek, loginUser, user);
  }

  private List<DduduCompletionResponse> generateCompletions(
      LocalDate startDate, LocalDate endDate, User loginUser, User user
  ) {
    List<PrivacyType> privacyTypes = determinePrivacyTypes(loginUser, user);

    Map<LocalDate, DduduCompletionResponse> completionByDate = dduduStatsPort.calculateDdudusCompletion(
            startDate, endDate, user, privacyTypes)
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

  private List<PrivacyType> determinePrivacyTypes(User loginUser, User user) {
    if (loginUser.equals(user)) {
      return List.of(PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC);
    }

    if (isFollowerOf(loginUser, user)) {
      return List.of(PrivacyType.FOLLOWER, PrivacyType.PUBLIC);
    }

    return List.of(PrivacyType.PUBLIC);
  }

  private boolean isFollowerOf(User user, User targetUser) {
    // TODO: 팔로잉 기능 추가 시 팔로잉 상태 확인
    return false;
  }

}
