package com.ddudu.application.port.in.repeatable_ddudu;

import com.ddudu.application.dto.repeatable_ddudu.requset.CreateRepeatableDduduRequest;

public interface CreateRepeatableDduduUseCase {

  Long create(Long loginId, CreateRepeatableDduduRequest request);

}
