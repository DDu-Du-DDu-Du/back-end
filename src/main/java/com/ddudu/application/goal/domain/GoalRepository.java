package com.ddudu.application.goal.domain;

import com.ddudu.application.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository {

  Goal save(Goal goal);

  Optional<Goal> findById(Long id);

  List<Goal> findAllByUser(User user);

  List<Goal> findAllByUserAndPrivacyTypes(
      User user, List<PrivacyType> privacyTypes
  );

  void update(Goal goal);


}
