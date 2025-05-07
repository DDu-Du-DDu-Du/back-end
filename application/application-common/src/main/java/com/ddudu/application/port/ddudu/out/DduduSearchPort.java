package com.ddudu.application.port.ddudu.out;

import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;

public interface DduduSearchPort {

  ScrollResponse<SimpleDduduSearchDto> search(
      Long userId, ScrollRequest request, String query, Boolean isMine
  );

}
