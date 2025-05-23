package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;

public interface RepeatDduduPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  Ddudu save(Ddudu ddudu);

}
