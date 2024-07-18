package com.ddudu.application.port.out.goal;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import java.util.List;
import java.util.Optional;

public interface GoalLoaderPort {

  Goal getGoalOrElseThrow(Long id, String message);

  Optional<Goal> getOptionalGoal(Long id);

  List<Goal> findAllByUserAndPrivacyTypes(User user);

  List<Goal> findAllByUserAndPrivacyTypes(User user, List<PrivacyType> privacyTypes);

  List<Goal> findAccessibleGoals(User user, boolean isFollower);

}
