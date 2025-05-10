package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.response.DduduDetailResponse;

public interface RetrieveDduduUseCase {

  DduduDetailResponse findById(Long loginId, Long id);

}
