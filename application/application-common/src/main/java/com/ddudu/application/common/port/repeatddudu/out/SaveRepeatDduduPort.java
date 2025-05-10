package com.ddudu.application.common.port.repeatddudu.out;

import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;

public interface SaveRepeatDduduPort {

  RepeatDdudu save(RepeatDdudu repeatDdudu);

}
