package com.ddudu.infrastructure.persistence.repository.ddudu;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.persistence.entity.TodoEntity;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface DduduQueryRepository {

  List<TodoEntity> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, UserEntity user);

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  );

}
