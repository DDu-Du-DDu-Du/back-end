package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.dto.ddudu.response.RepeatAnotherDayResponse;

public interface RepeatUseCase {

  RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId, Long dduduId, RepeatAnotherDayRequest request
  );

}
