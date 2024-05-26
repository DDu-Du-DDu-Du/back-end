package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.domain.ddudu.dto.request.RepeatAnotherDayRequest;
import com.ddudu.application.domain.ddudu.dto.response.RepeatAnotherDayResponse;

public interface RepeatUseCase {

  RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId, Long dduduId, RepeatAnotherDayRequest request
  );

}
