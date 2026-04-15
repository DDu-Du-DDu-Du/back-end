package com.modoo.infra.mysql.planning.todo.adapter;

import com.modoo.aggregate.BaseStats;
import com.modoo.aggregate.MonthlyStats;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.application.common.dto.stats.GoalStatusSummaryRaw;
import com.modoo.application.common.dto.stats.RepeatTodoStatsDto;
import com.modoo.application.common.dto.stats.response.TodoCompletionResponse;
import com.modoo.application.common.dto.todo.TodoCursorDto;
import com.modoo.application.common.port.stats.out.GoalDetailStatsPort;
import com.modoo.application.common.port.stats.out.MonthlyStatsPort;
import com.modoo.application.common.port.stats.out.TodoStatsPort;
import com.modoo.application.common.port.todo.out.DeleteTodoPort;
import com.modoo.application.common.port.todo.out.RepeatTodoPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.todo.out.TodoSearchPort;
import com.modoo.application.common.port.todo.out.TodoUpdatePort;
import com.modoo.common.annotation.DrivenAdapter;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.infra.mysql.planning.todo.entity.TodoEntity;
import com.modoo.infra.mysql.planning.todo.repository.TodoRepository;
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

  private final TodoRepository todoRepository;

  @Override
  public Todo getTodoOrElseThrow(Long id, String message) {
    return todoRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            Todo.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

  @Override
  public Optional<Todo> getOptionalTodo(Long id) {
    return todoRepository.findById(id)
        .map(TodoEntity::toDomain);
  }

  @Override
  public List<Todo> getRepeatedTodos(RepeatTodo repeatTodo) {
    return todoRepository.findAllByRepeatTodoId(repeatTodo.getId())
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
    return todoRepository.findAllByDateAndUserAndPrivacyTypes(date, userId, accessiblePrivacyTypes)
        .stream()
        .map(TodoEntity::toDomain)
        .toList();
  }

  @Override
  public int countTodayTodo(Long userId) {
    return todoRepository.countTodayByUserId(userId);
  }

  @Override
  public Todo update(Todo todo) {
    TodoEntity todoEntity = todoRepository.findById(todo.getId())
        .orElseThrow(EntityNotFoundException::new);

    todoEntity.update(todo);

    return todoEntity.toDomain();
  }

  @Override
  public Todo save(Todo todo) {
    return todoRepository.save(TodoEntity.from(todo))
        .toDomain();
  }

  @Override
  public List<Todo> saveAll(List<Todo> todos) {
    List<TodoEntity> todoEntities = todos.stream()
        .map(TodoEntity::from)
        .toList();

    return todoRepository.saveAll(todoEntities)
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
    return todoRepository.findScrollTodos(
        userId,
        request,
        query,
        isMine,
        false
    );
  }

  @Override
  public void delete(Todo todo) {
    todoRepository.delete(TodoEntity.from(todo));
  }

  @Override
  public void deleteAllByRepeatTodo(RepeatTodo repeatTodo) {
    todoRepository.deleteAllByRepeatTodoId(repeatTodo.getId());
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
    return todoRepository.findTodosCompletion(
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
    return todoRepository.findGoalStatuses(userId, goalId);
  }

  @Override
  public Map<YearMonth, MonthlyStats> collectMonthlyStats(
      Long userId,
      Goal goal,
      LocalDate from,
      LocalDate to
  ) {
    Long goalId = Objects.nonNull(goal) ? goal.getId() : null;
    List<BaseStats> stats = todoRepository.findStatsBaseOfUser(userId, goalId, from, to);

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
    List<BaseStats> stats = todoRepository.findPostponedStatsBaseOfUser(userId, goalId, from, to);

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
    return todoRepository.countByRepeatTodoId(userId, goalId, from, to);
  }

}
