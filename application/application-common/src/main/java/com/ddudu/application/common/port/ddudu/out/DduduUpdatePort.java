package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;

public interface DduduUpdatePort {

  Ddudu update(Ddudu ddudu);

}
