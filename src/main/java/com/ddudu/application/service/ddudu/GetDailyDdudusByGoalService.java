package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.GoalGroupedDdudus;
import com.ddudu.application.domain.ddudu.dto.response.BasicDduduResponse;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
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

    if (Objects.equals(loginId, userId)) {
      List<Goal> goals = goalLoaderPort.findAllByUser(loginUser);
      return dduduLoaderPort.getDailyDdudusOfUserGroupingByGoal(date, loginUser, goals);
    }

    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    boolean isFollower = isFollowerOf(loginUser, user);
    List<Goal> accessibleGoals = goalLoaderPort.findAccessibleGoals(user, isFollower);
    return dduduLoaderPort.getDailyDdudusOfUserGroupingByGoal(date, user, accessibleGoals);
  }

  private boolean isFollowerOf(User user, User targetUser) {
    // TODO: 팔로잉 기능 추가 시 팔로잉 상태 확인
    return false;
  }

  private Map<Long, List<Ddudu>> groupDdudusByGoal(
      List<Goal> accessibleGoals, User user, LocalDate date
  ) {
    List<Ddudu> ddudus = dduduLoaderPort.getDailyDdudusOfUserUnderGoals(
        date, user, accessibleGoals);

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
