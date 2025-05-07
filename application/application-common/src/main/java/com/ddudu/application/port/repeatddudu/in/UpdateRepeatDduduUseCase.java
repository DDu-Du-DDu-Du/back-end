package com.ddudu.application.port.repeatddudu.in;

import com.ddudu.application.dto.repeatddudu.request.UpdateRepeatDduduRequest;

public interface UpdateRepeatDduduUseCase {

  Long update(Long loginId, Long id, UpdateRepeatDduduRequest request);

}
