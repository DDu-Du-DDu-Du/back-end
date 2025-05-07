package com.ddudu.application.port.repeatddudu.in;

import com.ddudu.application.dto.repeatddudu.request.CreateRepeatDduduRequest;

public interface CreateRepeatDduduUseCase {

  Long create(Long loginId, CreateRepeatDduduRequest request);

}
