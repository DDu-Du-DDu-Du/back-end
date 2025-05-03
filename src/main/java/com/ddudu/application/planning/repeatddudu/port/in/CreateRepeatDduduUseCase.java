package com.ddudu.application.planning.repeatddudu.port.in;

import com.ddudu.application.planning.repeatddudu.dto.request.CreateRepeatDduduRequest;

public interface CreateRepeatDduduUseCase {

  Long create(Long loginId, CreateRepeatDduduRequest request);

}
