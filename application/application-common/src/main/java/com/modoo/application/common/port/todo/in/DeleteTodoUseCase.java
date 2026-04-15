package com.modoo.application.common.port.todo.in;

public interface DeleteTodoUseCase {

  void delete(Long loginId, Long todoId);

}
