package com.ddudu.infra.mysql.planning.goal.adapter;

import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.application.port.goal.out.DeleteGoalPort;
import com.ddudu.application.port.goal.out.GoalLoaderPort;
import com.ddudu.application.port.goal.out.SaveGoalPort;
import com.ddudu.application.port.goal.out.UpdateGoalPort;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.infra.mysql.planning.ddudu.repository.DduduRepository;
import com.ddudu.infra.mysql.planning.goal.entity.GoalEntity;
import com.ddudu.infra.mysql.planning.goal.repository.GoalRepository;
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
  public List<Goal> findAllByUserAndPrivacyTypes(Long userId) {
    return goalRepository.findAllByUserId(userId)
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public List<Goal> findAllByUserAndPrivacyTypes(Long userId, List<PrivacyType> privacyTypes) {
    return goalRepository.findAllByUserAndPrivacyTypes(
            userId,
            privacyTypes
        )
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public List<Goal> findAccessibleGoals(Long userId, boolean isFollower) {
    List<PrivacyType> privacyTypes = Lists.newArrayList(PrivacyType.PUBLIC);

    if (isFollower) {
      privacyTypes.add(PrivacyType.FOLLOWER);
    }

    return goalRepository.findAllByUserAndPrivacyTypes(userId, privacyTypes)
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
