package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;

public interface DeleteDduduPort {

  void delete(Ddudu ddudu);

  void deleteAllByRepeatDdudu(RepeatDdudu repeatDdudu);

}
