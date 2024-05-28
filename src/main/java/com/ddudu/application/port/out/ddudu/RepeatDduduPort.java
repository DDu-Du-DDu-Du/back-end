package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;

public interface RepeatDduduPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

  Ddudu save(Ddudu ddudu);

}
