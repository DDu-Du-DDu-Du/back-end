package com.ddudu.application.port.ddudu.in;

import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.ddudu.request.DduduSearchRequest;

public interface DduduSearchUseCase {

  ScrollResponse<SimpleDduduSearchDto> search(Long loginId, DduduSearchRequest request);

}
