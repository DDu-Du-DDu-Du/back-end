package com.ddudu.application.service.ddudu;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.response.DduduInfo;
import com.ddudu.application.domain.ddudu.dto.response.DduduWithColorInfo;
import com.ddudu.application.domain.ddudu.dto.response.GoalGroupedDdudus;
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
public class GetTimetableService implements
    GetTimetableUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public TimetableResponse get(Long loginId, Long userId, LocalDate date) {
    User loginUser = getUserById(loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    User user = getUserById(userId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    List<Goal> accessibleGoals = getAccessibleGoals(loginUser, user);
    List<Ddudu> ddudus = getDdudusByDateAndUser(date, user, accessibleGoals);

    Map<LocalTime, List<DduduWithColorInfo>> dduduWithColorsByTime = groupDdudusWithColorByTime(
        ddudus, accessibleGoals);
    List<GoalGroupedDdudus> unassignedDdudus = groupUnassignedDdudusByGoal(ddudus, accessibleGoals);

    return TimetableResponse.of(dduduWithColorsByTime, unassignedDdudus);
  }

  private User getUserById(Long userId, String errorCode) {
    return userLoaderPort.getUserOrElseThrow(userId, errorCode);
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

  private List<Ddudu> getDdudusByDateAndUser(LocalDate date, User user, List<Goal> goals) {
    return dduduLoaderPort.findAllByDateAndUserAndGoals(date, user, goals);
  }

  private Map<LocalTime, List<DduduWithColorInfo>> groupDdudusWithColorByTime(
      List<Ddudu> ddudus, List<Goal> goals
  ) {

    Map<Long, String> colors = mapGoalColors(goals);
    List<LocalTime> times = extractDistinctSortedTimes(ddudus);

    return times.stream()
        .collect(Collectors.toMap(
            time -> time,
            time -> ddudus.stream()
                .filter(ddudu -> nonNull(ddudu.getBeginAt()) && ddudu.getBeginAt()
                    .equals(time))
                .map(ddudu -> DduduWithColorInfo.of(ddudu, colors.get(ddudu.getGoalId())))
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

  private Map<Long, String> mapGoalColors(List<Goal> goals) {
    return goals.stream()
        .collect(Collectors.toMap(Goal::getId, Goal::getColor));
  }

  private List<GoalGroupedDdudus> groupUnassignedDdudusByGoal(
      List<Ddudu> ddudus, List<Goal> goals
  ) {
    Map<Long, List<Ddudu>> unassignedDdudus = ddudus.stream()
        .filter(ddudu -> isNull(ddudu.getBeginAt()) || isNull(ddudu.getEndAt()))
        .collect(Collectors.groupingBy(Ddudu::getGoalId));

    return goals.stream()
        .map(goal -> toGoalGroupedDdudusResponse(goal, unassignedDdudus))
        .toList();
  }

  private GoalGroupedDdudus toGoalGroupedDdudusResponse(
      Goal goal, Map<Long, List<Ddudu>> ddudusByGoal
  ) {
    List<DduduInfo> dduduInfos = ddudusByGoal
        .getOrDefault(goal.getId(), Collections.emptyList())
        .stream()
        .sorted((a, b) -> Math.toIntExact(b.getId() - a.getId()))
        .map(DduduInfo::from)
        .toList();

    return GoalGroupedDdudus.of(goal, dduduInfos);
  }

}
