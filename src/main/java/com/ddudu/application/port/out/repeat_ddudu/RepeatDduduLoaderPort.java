package com.ddudu.application.port.out.repeat_ddudu;

import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import java.util.Optional;

public interface RepeatDduduLoaderPort {

  Optional<RepeatDdudu> getOptionalRepeatDdudu(Long id);

}
