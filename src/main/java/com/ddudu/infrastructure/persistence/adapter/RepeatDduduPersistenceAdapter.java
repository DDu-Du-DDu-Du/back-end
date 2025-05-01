package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.port.out.repeat_ddudu.DeleteRepeatDduduPort;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.SaveRepeatDduduPort;
import com.ddudu.application.port.out.repeat_ddudu.UpdateRepeatDduduPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.RepeatDduduEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import com.ddudu.infrastructure.persistence.repository.repeat_ddudu.RepeatDduduRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class RepeatDduduPersistenceAdapter implements SaveRepeatDduduPort, RepeatDduduLoaderPort,
    UpdateRepeatDduduPort, DeleteRepeatDduduPort {

  private final RepeatDduduRepository repeatDduduRepository;
  private final DduduRepository dduduRepository;

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

  @Override
  public RepeatDdudu getOrElseThrow(Long id, String message) {
    return repeatDduduRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            RepeatDdudu.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public RepeatDdudu update(RepeatDdudu repeatDdudu) {
    RepeatDduduEntity repeatDduduEntity = repeatDduduRepository.findById(repeatDdudu.getId())
        .orElseThrow(EntityNotFoundException::new);

    repeatDduduEntity.update(repeatDdudu);

    return repeatDduduEntity.toDomain();
  }

  @Override
  public void deleteWithDdudus(RepeatDdudu repeatDdudu) {
    dduduRepository.deleteAllByRepeatDduduId(repeatDdudu.getId());
    repeatDduduRepository.delete(RepeatDduduEntity.from(repeatDdudu));
  }

  @Override
  public void deleteAllWithDdudusByGoal(Goal goal) {
    dduduRepository.deleteAllByGoalId(goal.getId());
    repeatDduduRepository.deleteAllByGoal(goal.getId());
  }

}
