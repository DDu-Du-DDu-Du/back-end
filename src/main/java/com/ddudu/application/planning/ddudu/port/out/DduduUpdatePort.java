package com.ddudu.application.planning.ddudu.port.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;

public interface DduduUpdatePort {

  Ddudu update(Ddudu ddudu);

}
