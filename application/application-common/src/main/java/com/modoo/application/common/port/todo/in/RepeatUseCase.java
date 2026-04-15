package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.request.RepeatAnotherDayRequest;
import com.modoo.application.common.dto.todo.response.RepeatAnotherDayResponse;

public interface RepeatUseCase {

  RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId,
      Long todoId,
      RepeatAnotherDayRequest request
  );

}
