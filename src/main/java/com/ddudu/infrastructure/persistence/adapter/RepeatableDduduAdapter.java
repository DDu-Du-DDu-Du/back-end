package com.ddudu.infrastructure.persistence.adapter;

import com.ddudu.application.domain.repeatable_ddudu.domain.RepeatableDdudu;
import com.ddudu.application.port.out.repeatable_ddudu.SaveRepeatableDduduPort;
import com.ddudu.infrastructure.annotation.DrivenAdapter;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class RepeatableDduduAdapter implements SaveRepeatableDduduPort {

  @Override
  public RepeatableDdudu save(RepeatableDdudu repeatableDdudu) {
    return null;
  }

}
