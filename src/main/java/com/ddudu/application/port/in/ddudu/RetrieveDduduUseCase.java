package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.ddudu.response.DduduDetailResponse;

public interface RetrieveDduduUseCase {

  DduduDetailResponse findById(Long loginId, Long id);

}
