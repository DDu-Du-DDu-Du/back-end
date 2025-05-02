package com.ddudu.infrastructure.persistence.repository.period_goal;

import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.infrastructure.persistence.entity.PeriodGoalEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodGoalRepository extends JpaRepository<PeriodGoalEntity, Long> {

  Optional<PeriodGoalEntity> findByUserIdAndPlanDateAndType(
      Long userId, LocalDate planDate, PeriodGoalType type
  );

}
