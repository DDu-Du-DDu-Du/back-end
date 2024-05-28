package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.ddudu.request.MoveDateRequest;

public interface MoveDateUseCase {

  void moveDate(Long loginId, Long dduduId, MoveDateRequest request);

}
