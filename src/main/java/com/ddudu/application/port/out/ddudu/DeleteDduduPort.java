package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;

public interface DeleteDduduPort {

  void delete(Ddudu ddudu);

  void deleteAllByRepeatDdudu(RepeatDdudu repeatDdudu);

}
