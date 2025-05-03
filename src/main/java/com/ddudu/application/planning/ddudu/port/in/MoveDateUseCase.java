package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.request.MoveDateRequest;

public interface MoveDateUseCase {

  void moveDate(Long loginId, Long dduduId, MoveDateRequest request);

}
