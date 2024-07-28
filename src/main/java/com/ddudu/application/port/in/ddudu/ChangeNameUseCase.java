package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.ddudu.request.ChangeNameRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;

public interface ChangeNameUseCase {

  BasicDduduResponse change(Long loginId, Long dduduId, ChangeNameRequest request);

}
