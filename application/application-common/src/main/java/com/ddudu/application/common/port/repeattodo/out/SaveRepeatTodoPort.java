package com.ddudu.application.common.port.repeattodo.out;

import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;

public interface SaveRepeatTodoPort {

  RepeatTodo save(RepeatTodo repeatTodo);

}
