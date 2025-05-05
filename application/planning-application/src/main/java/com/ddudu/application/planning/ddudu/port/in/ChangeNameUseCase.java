package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.request.ChangeNameRequest;
import com.ddudu.application.planning.ddudu.dto.response.BasicDduduResponse;

public interface ChangeNameUseCase {

  BasicDduduResponse change(Long loginId, Long dduduId, ChangeNameRequest request);

}
