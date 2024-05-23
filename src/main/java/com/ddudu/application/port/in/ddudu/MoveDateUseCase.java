package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.domain.ddudu.dto.request.MoveDateRequest;

public interface MoveDateUseCase {

  void moveDate(Long loginId, Long dduduId, MoveDateRequest request);

}
