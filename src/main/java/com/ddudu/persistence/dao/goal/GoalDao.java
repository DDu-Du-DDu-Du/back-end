package com.ddudu.persistence.dao.goal;

import com.ddudu.persistence.entity.GoalEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalDao extends JpaRepository<GoalEntity, Long>, GoalDaoCustom {

  Optional<GoalEntity> findById(Long id);

}
