package com.ddudu.old.persistence.dao.goal;

import com.ddudu.old.goal.domain.PrivacyType;
import com.ddudu.old.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import java.util.List;

public interface GoalDaoCustom {

  List<GoalEntity> findAllByUser(UserEntity user);

  List<GoalEntity> findAllByUserAndPrivacyTypes(UserEntity user, List<PrivacyType> privacyTypes);

}
