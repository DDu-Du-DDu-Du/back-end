package com.ddudu.application.port.ddudu.in;

import com.ddudu.application.dto.ddudu.request.MoveDateRequest;

public interface MoveDateUseCase {

  void moveDate(Long loginId, Long dduduId, MoveDateRequest request);

}
