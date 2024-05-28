package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.dto.scroll.response.ScrollResponse;

public interface DduduSearchUseCase {

  ScrollResponse<SimpleDduduSearchDto> search(Long loginId, DduduSearchRequest request);

}
