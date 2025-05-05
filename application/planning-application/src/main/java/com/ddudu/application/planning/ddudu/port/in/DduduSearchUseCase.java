package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.SimpleDduduSearchDto;
import com.ddudu.application.planning.ddudu.dto.request.DduduSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface DduduSearchUseCase {

  ScrollResponse<SimpleDduduSearchDto> search(Long loginId, DduduSearchRequest request);

}
