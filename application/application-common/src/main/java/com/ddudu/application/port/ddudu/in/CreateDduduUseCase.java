package com.ddudu.application.port.ddudu.in;

import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;

public interface CreateDduduUseCase {

  BasicDduduResponse create(Long loginId, CreateDduduRequest request);

}
