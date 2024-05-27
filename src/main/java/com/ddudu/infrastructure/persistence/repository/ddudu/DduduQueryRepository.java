package com.ddudu.infrastructure.persistence.repository.ddudu;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.dto.scroll.request.ScrollRequest;
import com.ddudu.infrastructure.persistence.dto.DduduCursorDto;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface DduduQueryRepository {

  List<DduduEntity> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user
  );

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  );

  void deleteAllByGoal(GoalEntity goal);

  List<DduduCursorDto> findScrollMyDdudus(
      Long userId, ScrollRequest request, String query, Boolean isMine, Boolean isFollower
  );

}
