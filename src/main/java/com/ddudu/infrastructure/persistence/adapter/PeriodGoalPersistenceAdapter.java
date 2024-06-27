package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.period_goal.PeriodGoalLoaderPort;
import com.ddudu.application.port.out.period_goal.SavePeriodGoalPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.PeriodGoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.infrastructure.persistence.repository.period_goal.PeriodGoalRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class PeriodGoalPersistenceAdapter implements SavePeriodGoalPort, PeriodGoalLoaderPort {

  private final PeriodGoalRepository periodGoalRepository;

  @Override
  public PeriodGoal save(PeriodGoal periodGoal) {
    return periodGoalRepository.save(PeriodGoalEntity.from(periodGoal))
        .toDomain();
  }

  @Override
  public Optional<PeriodGoal> getOptionalByDate(User user, LocalDate date, PeriodGoalType type) {
    return periodGoalRepository.findByUserAndPlanDateAndType(
            UserEntity.from(user),
            date,
            type.name()
        )
        .map(PeriodGoalEntity::toDomain);
  }

}
