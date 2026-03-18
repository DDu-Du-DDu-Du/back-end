package com.ddudu.application.common.port.repeattodo.out;

import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;

public interface UpdateRepeatTodoPort {

  RepeatTodo update(RepeatTodo repeatTodo);

}
