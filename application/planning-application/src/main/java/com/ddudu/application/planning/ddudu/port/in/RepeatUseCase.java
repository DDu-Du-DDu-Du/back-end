package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.request.RepeatAnotherDayRequest;
import com.ddudu.application.planning.ddudu.dto.response.RepeatAnotherDayResponse;

public interface RepeatUseCase {

  RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId, Long dduduId, RepeatAnotherDayRequest request
  );

}
