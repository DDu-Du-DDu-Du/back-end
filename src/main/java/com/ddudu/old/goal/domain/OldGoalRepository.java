package com.ddudu.old.goal.domain;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface OldGoalRepository {

  Goal save(Goal goal);

  Optional<Goal> findById(Long id);

  List<Goal> findAllByUser(User user);

  List<Goal> findAllByUserAndPrivacyTypes(
      User user, List<PrivacyType> privacyTypes
  );

  void update(Goal goal);

  void delete(Goal goal);

}
