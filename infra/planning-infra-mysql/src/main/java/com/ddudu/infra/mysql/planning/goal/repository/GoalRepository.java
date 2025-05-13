package com.ddudu.infra.mysql.planning.goal.repository;

import com.ddudu.infra.mysql.planning.goal.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Long>, GoalQueryRepository {

}
