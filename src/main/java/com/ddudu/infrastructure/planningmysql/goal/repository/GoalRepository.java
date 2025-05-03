package com.ddudu.infrastructure.planningmysql.goal.repository;

import com.ddudu.infrastructure.planningmysql.goal.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Long>, GoalQueryRepository {

}
