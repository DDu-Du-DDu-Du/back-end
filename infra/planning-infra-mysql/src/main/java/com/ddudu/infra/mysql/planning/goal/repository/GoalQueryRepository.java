package com.ddudu.infra.mysql.planning.goal.repository;

import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.infra.mysql.planning.goal.entity.GoalEntity;
import java.util.List;

public interface GoalQueryRepository {

  List<GoalEntity> findAllByUserId(Long userId);

  List<GoalEntity> findAllByUserAndPrivacyTypes(Long userId, List<PrivacyType> privacyTypes);

}
