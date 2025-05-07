package com.ddudu.application.port.ddudu.out;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import java.util.List;

public interface SaveDduduPort {

  Ddudu save(Ddudu ddudu);

  List<Ddudu> saveAll(List<Ddudu> dduduList);

}
