package com.ddudu.persistence.dao.todo;

import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.persistence.entity.TodoEntity;
import com.ddudu.persistence.entity.UserEntity;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface TodoDaoCustom {

  List<TodoEntity> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, UserEntity user);

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  );

}