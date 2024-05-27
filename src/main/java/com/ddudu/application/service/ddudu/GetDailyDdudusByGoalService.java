package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.GoalGroupedDdudus;
import com.ddudu.application.domain.ddudu.dto.response.BasicDduduResponse;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.ddudu.GetDailyDdudusByGoalUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDailyDdudusByGoalService implements GetDailyDdudusByGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public List<GoalGroupedDdudus> get(Long loginId, Long userId, LocalDate date) {
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    List<Goal> accessibleGoals = getAccessibleGoals(loginUser, user);
    Map<Long, List<Ddudu>> ddudusByGoal = groupDdudusByGoal(accessibleGoals, user, date);

    return accessibleGoals.stream()
        .map(goal -> toGoalGroupedDdudusResponse(goal, ddudusByGoal))
        .toList();
  }

  private List<Goal> getAccessibleGoals(User requestingUser, User targetUser) {
    return goalLoaderPort.findAllByUserAndPrivacyTypes(
        targetUser, determinePrivacyTypes(requestingUser, targetUser));
  }

  private List<PrivacyType> determinePrivacyTypes(User loginUser, User user) {
    if (Objects.equals(loginUser, user)) {
      return List.of(PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC);
    }

    // TODO: 팔로잉 기능 추가 시 팔로잉 상태 확인

    return List.of(PrivacyType.PUBLIC);
  }

  private Map<Long, List<Ddudu>> groupDdudusByGoal(
      List<Goal> accessibleGoals, User user, LocalDate date
  ) {
    List<Ddudu> ddudus = dduduLoaderPort.findAllByDateAndUserAndGoals(date, user, accessibleGoals);

    return ddudus.stream()
        .collect(Collectors.groupingBy(Ddudu::getGoalId));
  }

  private GoalGroupedDdudus toGoalGroupedDdudusResponse(
      Goal goal, Map<Long, List<Ddudu>> ddudusByGoal
  ) {
    List<BasicDduduResponse> basicDduduResponses = ddudusByGoal
        .getOrDefault(goal.getId(), Collections.emptyList())
        .stream()
        .sorted((a, b) -> Math.toIntExact(b.getId() - a.getId()))
        .map(BasicDduduResponse::from)
        .toList();

    return GoalGroupedDdudus.of(goal, basicDduduResponses);
  }

}
