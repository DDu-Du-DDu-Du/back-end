package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.GoalLoaderPort;
import com.ddudu.application.port.out.SaveGoalPort;
import com.ddudu.application.port.out.UpdateGoalPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.goal.GoalRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class GoalPersistenceAdapter implements SaveGoalPort, GoalLoaderPort, UpdateGoalPort {

  private final GoalRepository goalRepository;

  @Override
  public Goal save(Goal goal) {
    return goalRepository.save(GoalEntity.from(goal))
        .toDomain();
  }

  @Override
  public Optional<Goal> findById(Long id) {
    return goalRepository.findById(id)
        .map(GoalEntity::toDomain);
  }

  @Override
  public List<Goal> findAllByUser(User user) {
    return goalRepository.findAllByUser(UserEntity.from(user))
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public Goal update(Goal goal) {
    return goalRepository.save(GoalEntity.from(goal))
        .toDomain();
  }

}
