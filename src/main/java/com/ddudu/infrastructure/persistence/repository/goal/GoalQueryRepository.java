package com.ddudu.infrastructure.persistence.repository.goal;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import java.util.List;

public interface GoalQueryRepository {

  List<GoalEntity> findAllByUser(UserEntity user);

  List<GoalEntity> findAllByUserAndPrivacyTypes(UserEntity user, List<PrivacyType> privacyTypes);

  void update(GoalEntity goal);

}
