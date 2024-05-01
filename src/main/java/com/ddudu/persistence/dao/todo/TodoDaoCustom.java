package com.ddudu.persistence.dao.todo;

import com.ddudu.application.goal.domain.PrivacyType;
import com.ddudu.application.todo.dto.response.TodoCompletionResponse;
import com.ddudu.persistence.entity.TodoEntity;
import com.ddudu.persistence.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;

public interface TodoDaoCustom {

  List<TodoEntity> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, UserEntity user);

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  );

}
