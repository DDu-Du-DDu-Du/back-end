package com.ddudu.infra.mysql.planning.periodgoal.repository;

import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.infra.mysql.planning.periodgoal.entity.PeriodGoalEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodGoalRepository extends JpaRepository<PeriodGoalEntity, Long> {

  Optional<PeriodGoalEntity> findByUserIdAndPlanDateAndType(
      Long userId, LocalDate planDate, PeriodGoalType type
  );

}
