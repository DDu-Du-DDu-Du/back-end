package com.ddudu.goal.repository;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.user.domain.User;
import java.util.List;

public interface GoalRepositoryCustom {

  List<Goal> findAllByUser(User user);

  List<Goal> findAllByUserAndPrivacyType(User user, PrivacyType privacyType);

}
