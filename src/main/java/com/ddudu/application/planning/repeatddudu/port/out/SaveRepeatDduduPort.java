package com.ddudu.application.planning.repeatddudu.port.out;

import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;

public interface SaveRepeatDduduPort {

  RepeatDdudu save(RepeatDdudu repeatDdudu);

}
