package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import java.util.List;

public interface SaveDduduPort {

  Ddudu save(Ddudu ddudu);

  List<Ddudu> saveAll(List<Ddudu> dduduList);

}
