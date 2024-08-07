package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.SaveRepeatDduduPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.RepeatDduduEntity;
import com.ddudu.infrastructure.persistence.repository.repeat_ddudu.RepeatDduduRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class RepeatDduduPersistenceAdapter implements SaveRepeatDduduPort, RepeatDduduLoaderPort {

  private final RepeatDduduRepository repeatDduduRepository;

  @Override
  public RepeatDdudu save(RepeatDdudu repeatDdudu) {
    return repeatDduduRepository.save(RepeatDduduEntity.from(repeatDdudu))
        .toDomain();
  }


  @Override
  public Optional<RepeatDdudu> getOptionalRepeatDdudu(Long id) {
    return repeatDduduRepository.findById(id)
        .map(RepeatDduduEntity::toDomain);
  }

  @Override
  public List<RepeatDdudu> getAllByGoal(Goal goal) {
    return repeatDduduRepository.findAllByGoal(GoalEntity.from(goal))
        .stream()
        .map(RepeatDduduEntity::toDomain)
        .toList();
  }

}
