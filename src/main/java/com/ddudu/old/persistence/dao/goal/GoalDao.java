package com.ddudu.old.persistence.dao.goal;

import com.ddudu.old.persistence.entity.GoalEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalDao extends JpaRepository<GoalEntity, Long>, GoalDaoCustom {

  Optional<GoalEntity> findById(Long id);

}
