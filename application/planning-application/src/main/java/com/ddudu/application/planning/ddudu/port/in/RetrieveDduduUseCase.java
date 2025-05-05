package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.response.DduduDetailResponse;

public interface RetrieveDduduUseCase {

  DduduDetailResponse findById(Long loginId, Long id);

}
