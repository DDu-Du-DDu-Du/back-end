package com.modoo.application.common.port.repeattodo.out;

import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;

public interface UpdateRepeatTodoPort {

  RepeatTodo update(RepeatTodo repeatTodo);

}
