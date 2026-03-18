package com.ddudu.infra.mysql.planning.todo.adapter;

import com.ddudu.aggregate.BaseStats;
import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.todo.TodoCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.stats.GoalStatusSummaryRaw;
import com.ddudu.application.common.dto.stats.RepeatTodoStatsDto;
import com.ddudu.application.common.dto.stats.response.TodoCompletionResponse;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoSearchPort;
import com.ddudu.application.common.port.todo.out.TodoUpdatePort;
import com.ddudu.application.common.port.todo.out.DeleteTodoPort;
import com.ddudu.application.common.port.todo.out.RepeatTodoPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.stats.out.TodoStatsPort;
import com.ddudu.application.common.port.stats.out.GoalDetailStatsPort;
import com.ddudu.application.common.port.stats.out.MonthlyStatsPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.infra.mysql.planning.todo.entity.TodoEntity;
import com.ddudu.infra.mysql.planning.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class TodoPersistenceAdapter implements TodoLoaderPort, TodoUpdatePort, SaveTodoPort,
    RepeatTodoPort, TodoSearchPort, DeleteTodoPort, TodoStatsPort, MonthlyStatsPort,
    GoalDetailStatsPort {

  private final TodoRepository dduduRepository;

  @Override
  public Todo getTodoOrElseThrow(Long id, String message) {
    return dduduRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            Todo.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public Optional<Todo> getOptionalTodo(Long id) {
    return dduduRepository.findById(id)
        .map(TodoEntity::toDomain);
  }

  @Override
  public List<Todo> getRepeatedTodos(RepeatTodo repeatTodo) {
    return dduduRepository.findAllByRepeatTodoId(repeatTodo.getId())
        .stream()
        .map(TodoEntity::toDomain)
        .toList();
  }

  @Override
  public List<Todo> getDailyTodos(
      LocalDate date,
      Long userId,
      List<PrivacyType> accessiblePrivacyTypes
  ) {
    return dduduRepository.findAllByDateAndUserAndPrivacyTypes(date, userId, accessiblePrivacyTypes)
        .stream()
        .map(TodoEntity::toDomain)
        .toList();
  }

  @Override
  public int countTodayTodo(Long userId) {
    return dduduRepository.countTodayByUserId(userId);
  }

  @Override
  public Todo update(Todo todo) {
    TodoEntity dduduEntity = dduduRepository.findById(todo.getId())
        .orElseThrow(EntityNotFoundException::new);

    dduduEntity.update(todo);

    return dduduEntity.toDomain();
  }

  @Override
  public Todo save(Todo todo) {
    return dduduRepository.save(TodoEntity.from(todo))
        .toDomain();
  }

  @Override
  public List<Todo> saveAll(List<Todo> ddudus) {
    List<TodoEntity> dduduEntities = ddudus.stream()
        .map(TodoEntity::from)
        .toList();

    return dduduRepository.saveAll(dduduEntities)
        .stream()
        .map(TodoEntity::toDomain)
        .toList();
  }

  @Override
  public List<TodoCursorDto> search(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine
  ) {
    return dduduRepository.findScrollTodos(
        userId,
        request,
        query,
        isMine,
        false
    );
  }

  @Override
  public void delete(Todo todo) {
    dduduRepository.delete(TodoEntity.from(todo));
  }

  @Override
  public void deleteAllByRepeatTodo(RepeatTodo repeatTodo) {
    dduduRepository.deleteAllByRepeatTodoId(repeatTodo.getId());
  }

  @Override
  public List<TodoCompletionResponse> calculateTodosCompletion(
      LocalDate startDate,
      LocalDate endDate,
      Long userId,
      Long goalId,
      List<PrivacyType> privacyTypes,
      boolean isAchieved
  ) {
    return dduduRepository.findTodosCompletion(
        startDate,
        endDate,
        userId,
        goalId,
        privacyTypes,
        isAchieved
    );
  }


  @Override
  public List<GoalStatusSummaryRaw> loadGoalStatuses(Long userId, Long goalId) {
    return dduduRepository.findGoalStatuses(userId, goalId);
  }

  @Override
  public Map<YearMonth, MonthlyStats> collectMonthlyStats(
      Long userId,
      Goal goal,
      LocalDate from,
      LocalDate to
  ) {
    Long goalId = Objects.nonNull(goal) ? goal.getId() : null;
    List<BaseStats> stats = dduduRepository.findStatsBaseOfUser(userId, goalId, from, to);

    return collectByMonth(userId, stats);
  }

  @Override
  public Map<YearMonth, MonthlyStats> collectMonthlyPostponedStats(
      Long userId,
      Goal goal,
      LocalDate from,
      LocalDate to
  ) {
    Long goalId = Objects.nonNull(goal) ? goal.getId() : null;
    List<BaseStats> stats = dduduRepository.findPostponedStatsBaseOfUser(userId, goalId, from, to);

    return collectByMonth(userId, stats);
  }

  private Map<YearMonth, MonthlyStats> collectByMonth(Long userId, List<BaseStats> stats) {

    return stats.stream()
        .collect(Collectors.groupingBy(stat -> YearMonth.from(stat.getScheduledOn())))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
                Map.Entry::getKey,
                monthlyStatsEntry -> MonthlyStats.builder()
                    .userId(userId)
                    .yearMonth(monthlyStatsEntry.getKey())
                    .stats(monthlyStatsEntry.getValue())
                    .build()
            )
        );
  }

  @Override
  public List<RepeatTodoStatsDto> countRepeatTodo(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  ) {
    return dduduRepository.countByRepeatTodoId(userId, goalId, from, to);
  }

}
