package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.port.out.period_goal.PeriodGoalLoaderPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.PeriodGoalEntity;
import com.ddudu.infrastructure.persistence.repository.period_goal.PeriodGoalRepository;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class PeriodGoalPersistenceAdapter implements PeriodGoalLoaderPort {

  private final PeriodGoalRepository periodGoalRepository;

  @Override
  public PeriodGoal save(PeriodGoal periodGoal) {
    return periodGoalRepository.save(PeriodGoalEntity.from(periodGoal))
        .toDomain();
  }

}
