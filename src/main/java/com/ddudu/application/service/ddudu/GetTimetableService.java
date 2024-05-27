package com.ddudu.application.service.ddudu;

import static java.util.Objects.nonNull;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.BasicDduduWithGoalId;
import com.ddudu.application.domain.ddudu.dto.GoalGroupedDdudus;
import com.ddudu.application.domain.ddudu.dto.response.TimetableResponse;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.ddudu.GetTimetableUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
    User user = userLoaderPort.getUserOrElseThrow(
        userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    List<Goal> accessibleGoals = getAccessibleGoals(loginUser, user);
    List<Ddudu> ddudus = dduduLoaderPort.getDailyDdudusOfUserUnderGoals(
        date, user, accessibleGoals);

    Map<LocalTime, List<BasicDduduWithGoalId>> ddudusWithGoalIdByTime = groupDdudusWithGoalIdByTime(
        ddudus, accessibleGoals);
    List<GoalGroupedDdudus> unassignedDdudus = dduduLoaderPort.getUnassignedDdudusOfUserGroupingByGoal(
        date, loginUser, accessibleGoals);

    return TimetableResponse.of(ddudusWithGoalIdByTime, unassignedDdudus);
  }

  private List<Goal> getAccessibleGoals(User requestingUser, User targetUser) {
    return goalLoaderPort.findAllByUser(
        targetUser, determinePrivacyTypes(requestingUser, targetUser));
  }

  private List<PrivacyType> determinePrivacyTypes(User loginUser, User user) {
    if (Objects.equals(loginUser, user)) {
      return List.of(PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC);
    }

    // TODO: 팔로잉 기능 추가 시 팔로잉 상태 확인

    return List.of(PrivacyType.PUBLIC);
  }

  private Map<LocalTime, List<BasicDduduWithGoalId>> groupDdudusWithGoalIdByTime(
      List<Ddudu> ddudus, List<Goal> goals
  ) {
    List<LocalTime> times = extractDistinctSortedTimes(ddudus);

    return times.stream()
        .collect(Collectors.toMap(
            time -> time,
            time -> ddudus.stream()
                .filter(ddudu -> nonNull(ddudu.getBeginAt()) && ddudu.getBeginAt()
                    .equals(time))
                .map(BasicDduduWithGoalId::of)
                .sorted((a, b) -> Math.toIntExact(b.id() - a.id()))
                .toList()
        ));
  }

  private List<LocalTime> extractDistinctSortedTimes(List<Ddudu> ddudus) {
    return ddudus.stream()
        .map(Ddudu::getBeginAt)
        .filter(Objects::nonNull)
        .sorted(LocalTime::compareTo)
        .distinct()
        .toList();
  }

}
