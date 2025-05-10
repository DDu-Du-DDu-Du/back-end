package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.ChangeNameRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;

public interface ChangeNameUseCase {

  BasicDduduResponse change(Long loginId, Long dduduId, ChangeNameRequest request);

}
