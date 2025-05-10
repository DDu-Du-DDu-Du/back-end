package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.MoveDateRequest;

public interface MoveDateUseCase {

  void moveDate(Long loginId, Long dduduId, MoveDateRequest request);

}
