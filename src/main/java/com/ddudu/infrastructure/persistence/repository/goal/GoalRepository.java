package com.ddudu.infrastructure.persistence.repository.goal;

import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Long>, GoalQueryRepository {

}
