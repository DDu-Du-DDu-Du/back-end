package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.UpdateDduduRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;

public interface UpdateDduduUseCase {

  BasicDduduResponse update(Long loginId, Long dduduId, UpdateDduduRequest request);

}
