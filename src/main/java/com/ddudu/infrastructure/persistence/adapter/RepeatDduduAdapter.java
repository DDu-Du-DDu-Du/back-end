package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.port.out.repeat_ddudu.SaveRepeatDduduPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import com.ddudu.infrastructure.persistence.entity.RepeatDduduEntity;
import com.ddudu.infrastructure.persistence.repository.repeat_ddudu.RepeatDduduRepository;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class RepeatDduduAdapter implements SaveRepeatDduduPort {

  private final RepeatDduduRepository repeatDduduRepository;

  @Override
  public RepeatDdudu save(RepeatDdudu repeatDdudu) {
    return repeatDduduRepository.save(RepeatDduduEntity.from(repeatDdudu))
        .toDomain();
  }

}
