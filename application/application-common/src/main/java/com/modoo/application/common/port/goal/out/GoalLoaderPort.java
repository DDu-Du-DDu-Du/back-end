package com.modoo.application.common.port.goal.out;

import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import java.util.List;
import java.util.Optional;

public interface GoalLoaderPort {

  Goal getGoalOrElseThrow(Long id, String message);

  Optional<Goal> getOptionalGoal(Long id);

  List<Goal> findAllByUserAndPrivacyTypes(Long userId);

  List<Goal> findAllByUserAndPrivacyTypes(Long userId, List<PrivacyType> privacyTypes);

  List<Goal> findAccessibleGoals(Long userId, boolean isFollower);

  int findMaxPriorityByUserId(Long userId);

}
