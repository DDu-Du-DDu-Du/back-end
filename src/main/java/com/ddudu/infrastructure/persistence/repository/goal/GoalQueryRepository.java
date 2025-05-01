package com.ddudu.infrastructure.persistence.repository.goal;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import java.util.List;

public interface GoalQueryRepository {

  List<GoalEntity> findAllByUserId(Long userId);

  List<GoalEntity> findAllByUserAndPrivacyTypes(Long userId, List<PrivacyType> privacyTypes);

}
