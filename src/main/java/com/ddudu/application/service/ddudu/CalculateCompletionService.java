package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.enums.Relationship;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.port.in.ddudu.CalculateCompletionUseCase;
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
public class CalculateCompletionService implements CalculateCompletionUseCase {

  private final UserLoaderPort userLoaderPort;
  private final DduduStatsPort dduduStatsPort;

  @Override
  public List<DduduCompletionResponse> calculate(
      Long loginId, Long userId, LocalDate from, LocalDate to
  ) {
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    return generateCompletions(from, to, loginUser, user);
  }

  private List<DduduCompletionResponse> generateCompletions(
      LocalDate startDate, LocalDate endDate, User loginUser, User user
  ) {
    Relationship relationship = Relationship.getRelationship(loginUser, user);
    List<PrivacyType> accessiblePrivacyTypes = PrivacyType.getAccessibleTypesIn(relationship);

    Map<LocalDate, DduduCompletionResponse> completionByDate = dduduStatsPort.calculateDdudusCompletion(
            startDate, endDate, user, accessiblePrivacyTypes)
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
