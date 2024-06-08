package com.ddudu.application.port.in.repeat_ddudu;

import com.ddudu.application.dto.repeat_ddudu.requset.CreateRepeatDduduRequest;

public interface CreateRepeatDduduUseCase {

  Long create(Long loginId, CreateRepeatDduduRequest request);

}
