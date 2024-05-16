package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.port.out.DeleteDduduPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import com.ddudu.infrastructure.persistence.repository.goal.GoalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class DduduPersistenceAdapter implements DeleteDduduPort {

  private final DduduRepository dduduRepository;
  private final GoalRepository goalRepository;

  @Override
  public void deleteAllByGoal(Goal goal) {
    GoalEntity goalEntity = goalRepository.findById(goal.getId())
        .orElseThrow(EntityNotFoundException::new);

    dduduRepository.deleteAllInBatch(dduduRepository.findAllByGoal(goalEntity));
  }

}
