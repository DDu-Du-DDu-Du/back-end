package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.response.DduduInfo;
import com.ddudu.application.domain.ddudu.dto.response.GoalGroupedDdudusResponse;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.port.in.ddudu.GetDailyDdudusByGoalUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
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

  @Override
  public List<GoalGroupedDdudusResponse> get(Long loginId, Long userId, LocalDate date) {
    List<Goal> goals = goalLoaderPort.findAllByUserAndPrivacyTypes(
        userId, determinePrivacyTypes(loginId, userId));

    List<Ddudu> ddudus = dduduLoaderPort.findAllByDateAndUserAndGoals(date, userId, goals);

    Map<Long, List<Ddudu>> todosByGoal = ddudus.stream()
        .collect(Collectors.groupingBy(Ddudu::getGoalId));

    return goals.stream()
        .map(goal -> toGoalGroupedDdudusResponse(goal, todosByGoal))
        .toList();
  }

  private List<PrivacyType> determinePrivacyTypes(Long loginUserId, Long userId) {
    if (Objects.equals(loginUserId, userId)) {
      return List.of(PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC);
    }

    // TODO: 팔로잉 기능 추가 시 팔로잉 상태 확인

    return List.of(PrivacyType.PUBLIC);
  }

  private GoalGroupedDdudusResponse toGoalGroupedDdudusResponse(
      Goal goal, Map<Long, List<Ddudu>> todosByGoal
  ) {
    List<DduduInfo> dduduInfos = todosByGoal
        .getOrDefault(goal.getId(), Collections.emptyList())
        .stream()
        .map(DduduInfo::from)
        .toList();

    return GoalGroupedDdudusResponse.from(goal, dduduInfos);
  }

}
