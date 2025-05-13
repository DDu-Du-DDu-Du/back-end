package com.ddudu.application.common.port.repeatddudu.in;

import com.ddudu.application.common.dto.repeatddudu.request.CreateRepeatDduduRequest;

public interface CreateRepeatDduduUseCase {

  Long create(Long loginId, CreateRepeatDduduRequest request);

}
