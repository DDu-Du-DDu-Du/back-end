package com.ddudu.goal.repository;

import com.ddudu.persistence.entity.GoalEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Long>, GoalRepositoryCustom {

  Optional<GoalEntity> findById(Long id);

}
