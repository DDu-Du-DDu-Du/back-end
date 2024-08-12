package com.ddudu.application.port.in.repeat_ddudu;

import com.ddudu.application.dto.repeat_ddudu.request.UpdateRepeatDduduRequest;

public interface UpdateRepeatDduduUseCase {

  Long update(Long loginId, Long id, UpdateRepeatDduduRequest request);

}
