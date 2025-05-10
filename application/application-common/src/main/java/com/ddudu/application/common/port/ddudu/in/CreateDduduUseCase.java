package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;

public interface CreateDduduUseCase {

  BasicDduduResponse create(Long loginId, CreateDduduRequest request);

}
