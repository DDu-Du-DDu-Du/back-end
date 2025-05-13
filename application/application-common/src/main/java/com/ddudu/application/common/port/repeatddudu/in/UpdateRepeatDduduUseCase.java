package com.ddudu.application.common.port.repeatddudu.in;

import com.ddudu.application.common.dto.repeatddudu.request.UpdateRepeatDduduRequest;

public interface UpdateRepeatDduduUseCase {

  Long update(Long loginId, Long id, UpdateRepeatDduduRequest request);

}
