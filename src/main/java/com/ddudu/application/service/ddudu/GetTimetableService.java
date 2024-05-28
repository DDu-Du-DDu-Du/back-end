package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.TimeGroupedDdudus;
import com.ddudu.application.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.port.in.ddudu.GetTimetableUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTimetableService implements
    GetTimetableUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public TimetableResponse get(Long loginId, Long userId, LocalDate date) {
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());

    if (Objects.equals(loginId, userId)) {
      List<Goal> goals = goalLoaderPort.findAllByUser(loginUser);

      List<TimeGroupedDdudus> ddudusWithGoalIdByTime = dduduLoaderPort.getDailyDdudusOfUserGroupingByTime(
          date, loginUser, goals);
      List<GoalGroupedDdudus> unassignedDdudus = dduduLoaderPort.getUnassignedDdudusOfUserGroupingByGoal(
          date, loginUser, goals);

      return TimetableResponse.of(ddudusWithGoalIdByTime, unassignedDdudus);
    }

    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    boolean isFollower = isFollowerOf(loginUser, user);
    List<Goal> accessibleGoals = goalLoaderPort.findAccessibleGoals(user, isFollower);

    List<TimeGroupedDdudus> ddudusWithGoalIdByTime = dduduLoaderPort.getDailyDdudusOfUserGroupingByTime(
        date, user, accessibleGoals);
    List<GoalGroupedDdudus> unassignedDdudus = dduduLoaderPort.getUnassignedDdudusOfUserGroupingByGoal(
        date, user, accessibleGoals);

    return TimetableResponse.of(ddudusWithGoalIdByTime, unassignedDdudus);
  }

  private boolean isFollowerOf(User user, User targetUser) {
    // TODO: 팔로잉 기능 추가 시 팔로잉 상태 확인
    return false;
  }

}
