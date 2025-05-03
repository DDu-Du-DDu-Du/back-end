package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.request.CreateDduduRequest;
import com.ddudu.application.planning.ddudu.dto.response.BasicDduduResponse;

public interface CreateDduduUseCase {

  BasicDduduResponse create(Long loginId, CreateDduduRequest request);

}
