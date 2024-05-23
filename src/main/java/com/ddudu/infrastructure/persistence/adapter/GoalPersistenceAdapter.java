package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.goal.DeleteGoalPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.goal.UpdateGoalPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import com.ddudu.infrastructure.persistence.repository.goal.GoalRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class GoalPersistenceAdapter implements SaveGoalPort, GoalLoaderPort, UpdateGoalPort,
    DeleteGoalPort {

  private final GoalRepository goalRepository;
  private final DduduRepository dduduRepository;

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
  public List<Goal> findAllByUserAndPrivacyTypes(Long userId, List<PrivacyType> privacyTypes) {
    return goalRepository.findAllByUserAndPrivacyTypes(
            UserEntity.withOnlyId(userId),
            privacyTypes
        )
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public Goal update(Goal goal) {
    GoalEntity goalEntity = goalRepository.findById(goal.getId())
        .orElseThrow(EntityNotFoundException::new);

    goalEntity.update(goal);

    return goalEntity.toDomain();
  }

  @Override
  public void deleteWithDdudus(Goal goal) {
    dduduRepository.deleteAllByGoal(GoalEntity.from(goal));
    goalRepository.delete(GoalEntity.from(goal));
  }

}
