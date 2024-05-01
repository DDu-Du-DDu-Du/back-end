package com.ddudu.persistence.repository;

import com.ddudu.application.goal.domain.Goal;
import com.ddudu.application.goal.domain.GoalRepository;
import com.ddudu.application.goal.domain.PrivacyType;
import com.ddudu.application.user.domain.User;
import com.ddudu.persistence.dao.goal.GoalDao;
import com.ddudu.persistence.entity.GoalEntity;
import com.ddudu.persistence.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {

  private final GoalDao goalDao;

  @Override
  public Goal save(Goal goal) {
    return goalDao.save(GoalEntity.from(goal))
        .toDomain();
  }

  @Override
  public Optional<Goal> findById(Long id) {
    return goalDao.findById(id)
        .map(GoalEntity::toDomain);
  }

  @Override
  public List<Goal> findAllByUser(User user) {
    return goalDao.findAllByUser(UserEntity.from(user))
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public List<Goal> findAllByUserAndPrivacyTypes(User user, List<PrivacyType> privacyTypes) {
    return goalDao.findAllByUserAndPrivacyTypes(UserEntity.from(user), privacyTypes)
        .stream()
        .map(GoalEntity::toDomain)
        .toList();
  }

  @Override
  public void update(Goal goal) {
    goalDao.save(GoalEntity.from(goal));
  }

}
