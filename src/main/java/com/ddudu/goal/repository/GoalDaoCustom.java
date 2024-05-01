package com.ddudu.goal.repository;

import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.persistence.entity.GoalEntity;
import com.ddudu.persistence.entity.UserEntity;
import java.util.List;

public interface GoalDaoCustom {

  List<GoalEntity> findAllByUser(UserEntity user);

  List<GoalEntity> findAllByUserAndPrivacyTypes(UserEntity user, List<PrivacyType> privacyTypes);

}
