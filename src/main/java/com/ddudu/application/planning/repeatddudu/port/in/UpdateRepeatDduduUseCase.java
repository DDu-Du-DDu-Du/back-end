package com.ddudu.application.planning.repeatddudu.port.in;

import com.ddudu.application.planning.repeatddudu.dto.request.UpdateRepeatDduduRequest;

public interface UpdateRepeatDduduUseCase {

  Long update(Long loginId, Long id, UpdateRepeatDduduRequest request);

}
