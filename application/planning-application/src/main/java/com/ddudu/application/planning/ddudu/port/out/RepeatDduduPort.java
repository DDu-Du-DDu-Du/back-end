package com.ddudu.application.planning.ddudu.port.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;

public interface RepeatDduduPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  Ddudu save(Ddudu ddudu);

}
