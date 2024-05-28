package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.scroll.request.ScrollRequest;
import com.ddudu.application.dto.scroll.response.ScrollResponse;

public interface DduduSearchPort {

  ScrollResponse<SimpleDduduSearchDto> search(
      Long userId, ScrollRequest request, String query, Boolean isMine
  );

}
