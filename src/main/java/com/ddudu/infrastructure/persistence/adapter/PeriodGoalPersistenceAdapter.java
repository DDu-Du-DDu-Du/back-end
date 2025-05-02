package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.period_goal.PeriodGoalLoaderPort;
import com.ddudu.application.port.out.period_goal.SavePeriodGoalPort;
import com.ddudu.application.port.out.period_goal.UpdatePeriodGoalPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.PeriodGoalEntity;
import com.ddudu.infrastructure.persistence.repository.period_goal.PeriodGoalRepository;
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
