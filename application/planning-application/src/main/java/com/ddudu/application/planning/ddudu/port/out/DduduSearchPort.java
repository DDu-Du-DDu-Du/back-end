package com.ddudu.application.planning.ddudu.port.out;

import com.ddudu.application.planning.ddudu.dto.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface DduduSearchPort {

  ScrollResponse<SimpleDduduSearchDto> search(
      Long userId, ScrollRequest request, String query, Boolean isMine
  );

}
