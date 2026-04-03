package com.ddudu.application.stats.service;

import static java.util.Objects.nonNull;

import com.ddudu.application.common.dto.stats.GoalStatusSummaryRaw;
import com.ddudu.application.common.dto.stats.response.GoalDetailStatsSummaryResponse;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.stats.in.CollectGoalDetailStatsUseCase;
import com.ddudu.application.common.port.stats.out.GoalDetailStatsPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectGoalDetailStatsService implements CollectGoalDetailStatsUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final GoalDetailStatsPort goalDetailStatsPort;

  @Override
  public GoalDetailStatsSummaryResponse collectDetail(Long loginId, Long goalId, Long userId) {
    Long targetUserId = nonNull(userId) ? userId : loginId;

    userLoaderPort.getUserOrElseThrow(
        targetUserId,
        StatsErrorCode.USER_NOT_EXISTING.getCodeName());
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        goalId,
        StatsErrorCode.GOAL_NOT_EXISTING.getCodeName());

    List<GoalStatusSummaryRaw> statuses = goalDetailStatsPort.loadGoalStatuses(
        targetUserId,
        goalId);

    int totalCount = statuses.size();
    int completedCount = (int) statuses.stream()
        .filter(summary -> summary.status() == TodoStatus.COMPLETE)
        .count();
    int completeRate = totalCount == 0 ? 0 : (int) Math.round(completedCount * 100.0 / totalCount);

    return GoalDetailStatsSummaryResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .createdAt(goal.getCreatedAt())
        .totalCount(totalCount)
        .completedCount(completedCount)
        .completeRate(completeRate)
        .build();
  }

}
