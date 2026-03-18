package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.todo.response.RepeatAnotherDayResponse;

public interface RepeatUseCase {

  RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId,
      Long todoId,
      RepeatAnotherDayRequest request
  );

}
