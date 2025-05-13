package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface DduduSearchUseCase {

  ScrollResponse<SimpleDduduSearchDto> search(Long loginId, DduduSearchRequest request);

}
