package com.ddudu.application.common.port.goal.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import java.util.List;
import java.util.Optional;

public interface GoalLoaderPort {

  Goal getGoalOrElseThrow(Long id, String message);

  Optional<Goal> getOptionalGoal(Long id);

  List<Goal> findAllByUserAndPrivacyTypes(Long userId);

  List<Goal> findAllByUserAndPrivacyTypes(Long userId, List<PrivacyType> privacyTypes);

  List<Goal> findAccessibleGoals(Long userId, boolean isFollower);

}
