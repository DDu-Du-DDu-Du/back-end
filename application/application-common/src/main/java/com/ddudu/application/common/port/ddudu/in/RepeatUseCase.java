package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.ddudu.response.RepeatAnotherDayResponse;

public interface RepeatUseCase {

  RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId, Long dduduId, RepeatAnotherDayRequest request
  );

}
