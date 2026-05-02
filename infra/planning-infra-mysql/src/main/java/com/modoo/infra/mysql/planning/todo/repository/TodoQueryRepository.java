package com.modoo.infra.mysql.planning.todo.repository;

import com.modoo.aggregate.BaseStats;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.application.common.dto.stats.GoalStatusSummaryRaw;
import com.modoo.application.common.dto.stats.RepeatTodoStatsDto;
import com.modoo.application.common.dto.stats.response.TodoCompletionResponse;
import com.modoo.application.common.dto.todo.TodoCursorDto;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.infra.mysql.planning.todo.entity.TodoEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TodoQueryRepository {

  List<TodoEntity> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, Long userId);

  List<TodoCompletionResponse> findTodosCompletion(
      LocalDate startDate,
      LocalDate endDate,
      Long userId,
      Long goalId,
      List<PrivacyType> privacyTypes,
      boolean isAchieved
  );

  void deleteAllByGoalId(Long goalId);

  List<TodoCursorDto> findScrollTodos(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine,
      Boolean isFollower
  );

  List<TodoEntity> findAllByDateAndUserAndPrivacyTypes(
      LocalDate date,
      Long userId,
      List<PrivacyType> accessiblePrivacyTypes
  );

  void deleteAllByRepeatTodoId(Long repeatTodoId);

  List<BaseStats> findStatsBaseOfUser(Long userId, Long goalId, LocalDate from, LocalDate to);

  List<BaseStats> findPostponedStatsBaseOfUser(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  );

  List<RepeatTodoStatsDto> countByRepeatTodoId(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  );

  List<GoalStatusSummaryRaw> findGoalStatuses(Long userId, Long goalId);

  int countTodayByUserId(Long userId);

  List<TodoEntity> findAllByUserId(Long userId);

}
