package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.PeriodSetupPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.repository.ddudu.DduduRepository;
import com.ddudu.infrastructure.persistence.repository.goal.GoalRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class DduduPersistenceAdapter implements DduduLoaderPort, PeriodSetupPort, SaveDduduPort {

  private final DduduRepository dduduRepository;
  private final GoalRepository goalRepository;

  @Override
  public Ddudu getDduduOrElseThrow(Long id, String message) {
    return dduduRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            Ddudu.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public Ddudu updatePeriod(Ddudu ddudu) {
    DduduEntity dduduEntity = dduduRepository.findById(ddudu.getId())
        .orElseThrow(EntityNotFoundException::new);

    dduduEntity.update(ddudu);

    return dduduEntity.toDomain();
  }

  @Override
  public Ddudu save(Ddudu ddudu) {
    return dduduRepository.save(DduduEntity.from(ddudu))
        .toDomain();
  }

}
