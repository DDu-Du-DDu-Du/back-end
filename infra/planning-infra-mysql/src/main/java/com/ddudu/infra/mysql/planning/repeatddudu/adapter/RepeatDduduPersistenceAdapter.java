package com.ddudu.infra.mysql.planning.repeatddudu.adapter;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.application.planning.repeatddudu.port.out.DeleteRepeatDduduPort;
import com.ddudu.application.planning.repeatddudu.port.out.RepeatDduduLoaderPort;
import com.ddudu.application.planning.repeatddudu.port.out.SaveRepeatDduduPort;
import com.ddudu.application.planning.repeatddudu.port.out.UpdateRepeatDduduPort;
import com.ddudu.application.common.annotation.DrivenAdapter;
import com.ddudu.infra.mysql.planning.repeatddudu.entity.RepeatDduduEntity;
import com.ddudu.infra.mysql.planning.ddudu.repository.DduduRepository;
import com.ddudu.infra.mysql.planning.repeatddudu.repository.RepeatDduduRepository;
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
    return repeatDduduRepository.findAllByGoalId(goal.getId())
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
