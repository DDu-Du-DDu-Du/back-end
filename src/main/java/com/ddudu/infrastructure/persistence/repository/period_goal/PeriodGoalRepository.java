package com.ddudu.infrastructure.persistence.repository.period_goal;

import com.ddudu.infrastructure.persistence.entity.PeriodGoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodGoalRepository extends JpaRepository<PeriodGoalEntity, Long> {

  Optional<PeriodGoalEntity> findByUserAndPlanDateAndType(
      UserEntity user, LocalDate planDate, String type
  );

}
