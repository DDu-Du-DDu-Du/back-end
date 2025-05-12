package com.ddudu.application.common.port.ddudu.out;

import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface DduduSearchPort {

  ScrollResponse<SimpleDduduSearchDto> search(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine
  );

}
