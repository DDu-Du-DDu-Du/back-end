package com.ddudu.old.persistence.dao.todo;

import com.ddudu.old.goal.domain.PrivacyType;
import com.ddudu.old.persistence.entity.TodoEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface TodoDaoCustom {

  List<TodoEntity> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, UserEntity user);

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  );

}
