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
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import com.ddudu.infrastructure.persistence.repository.goal.GoalRepository;
import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.MissingResourceException;
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
  public List<Goal> saveAll(List<Goal> defaultGoals) {
    List<GoalEntity> goalEntities = defaultGoals.stream()
        .map(GoalEntity::from)
        .toList();

    return goalRepository.saveAll(goalEntities)
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public Goal getGoalOrElseThrow(Long id, String message) {
    return goalRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            Goal.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public Optional<Goal> getOptionalGoal(Long id) {
    return goalRepository.findById(id)
        .map(GoalEntity::toDomain);
  }

  @Override
  public List<Goal> findAllByUserAndPrivacyTypes(User user) {
    return goalRepository.findAllByUserId(user.getId())
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public List<Goal> findAllByUserAndPrivacyTypes(User user, List<PrivacyType> privacyTypes) {
    return goalRepository.findAllByUserAndPrivacyTypes(
            user.getId(),
            privacyTypes
        )
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public List<Goal> findAccessibleGoals(User user, boolean isFollower) {
    List<PrivacyType> privacyTypes = Lists.newArrayList(PrivacyType.PUBLIC);

    if (isFollower) {
      privacyTypes.add(PrivacyType.FOLLOWER);
    }

    return goalRepository.findAllByUserAndPrivacyTypes(user.getId(), privacyTypes)
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
    dduduRepository.deleteAllByGoalId(goal.getId());
    goalRepository.delete(GoalEntity.from(goal));
  }

}
