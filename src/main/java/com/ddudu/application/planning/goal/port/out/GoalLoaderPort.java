package com.ddudu.application.planning.goal.port.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import java.util.List;
import java.util.Optional;

public interface GoalLoaderPort {

  Goal getGoalOrElseThrow(Long id, String message);

  Optional<Goal> getOptionalGoal(Long id);

  List<Goal> findAllByUserAndPrivacyTypes(User user);

  List<Goal> findAllByUserAndPrivacyTypes(User user, List<PrivacyType> privacyTypes);

  List<Goal> findAccessibleGoals(User user, boolean isFollower);

}
