package com.ddudu.infra.mysql.planning.periodgoal.adapter;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.periodgoal.port.out.PeriodGoalLoaderPort;
import com.ddudu.application.planning.periodgoal.port.out.SavePeriodGoalPort;
import com.ddudu.application.planning.periodgoal.port.out.UpdatePeriodGoalPort;
import com.ddudu.application.common.annotation.DrivenAdapter;
import com.ddudu.infra.mysql.planning.periodgoal.entity.PeriodGoalEntity;
import com.ddudu.infra.mysql.planning.periodgoal.repository.PeriodGoalRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.MissingResourceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class PeriodGoalPersistenceAdapter implements SavePeriodGoalPort, PeriodGoalLoaderPort,
    UpdatePeriodGoalPort {

  private final PeriodGoalRepository periodGoalRepository;

  @Override
  public PeriodGoal save(PeriodGoal periodGoal) {
    return periodGoalRepository.save(PeriodGoalEntity.from(periodGoal))
        .toDomain();
  }

  @Override
  public Optional<PeriodGoal> getOptionalByDate(User user, LocalDate date, PeriodGoalType type) {
    return periodGoalRepository.findByUserIdAndPlanDateAndType(
            user.getId(),
            date,
            type
        )
        .map(PeriodGoalEntity::toDomain);
  }

  public PeriodGoal getOrElseThrow(Long id, String message) {
    return periodGoalRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            PeriodGoal.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public PeriodGoal update(PeriodGoal periodGoal) {
    PeriodGoalEntity periodGoalEntity = periodGoalRepository.findById(periodGoal.getId())
        .orElseThrow(EntityNotFoundException::new);

    periodGoalEntity.update(periodGoal);

    return periodGoalEntity.toDomain();
  }

}
