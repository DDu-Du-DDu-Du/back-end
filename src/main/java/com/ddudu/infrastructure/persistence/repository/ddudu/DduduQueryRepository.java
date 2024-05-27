package com.ddudu.infrastructure.persistence.repository.ddudu;

import com.ddudu.application.domain.ddudu.dto.GoalGroupedDdudus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DduduQueryRepository {

  List<DduduEntity> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user
  );

  List<DduduEntity> findDdudusByDateAndUserAndGoals(
      LocalDate date, UserEntity user, List<GoalEntity> goals
  );

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  );

  void deleteAllByGoal(GoalEntity goal);

  List<GoalGroupedDdudus> findDailyDdudusByUserGroupByGoal(
      LocalDate date, UserEntity user, List<GoalEntity> goals
  );

}
