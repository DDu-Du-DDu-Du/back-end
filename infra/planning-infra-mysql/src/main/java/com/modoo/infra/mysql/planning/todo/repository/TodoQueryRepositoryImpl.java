package com.modoo.infra.mysql.planning.todo.repository;

import static com.modoo.infra.mysql.planning.goal.entity.QGoalEntity.goalEntity;
import static com.modoo.infra.mysql.planning.repeattodo.entity.QRepeatTodoEntity.repeatTodoEntity;
import static com.modoo.infra.mysql.planning.todo.entity.QTodoEntity.todoEntity;

import com.modoo.aggregate.BaseStats;
import com.modoo.application.common.dto.scroll.OrderType;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.application.common.dto.stats.GoalStatusSummaryRaw;
import com.modoo.application.common.dto.stats.RepeatTodoStatsDto;
import com.modoo.application.common.dto.stats.response.TodoCompletionResponse;
import com.modoo.application.common.dto.todo.SimpleTodoSearchDto;
import com.modoo.application.common.dto.todo.TodoCursorDto;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.planning.todo.aggregate.enums.TodoStatus;
import com.modoo.infra.mysql.planning.todo.entity.TodoEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  @Override
  public List<TodoEntity> findTodosByDate(
      LocalDateTime startDate,
      LocalDateTime endDate,
      Long userId
  ) {
    return jpaQueryFactory
        .selectFrom(todoEntity)
        .join(goalEntity)
        .on(todoEntity.goalId.eq(goalEntity.id))
        .where(
            todoEntity.beginAt.goe(LocalTime.from(startDate)),
            todoEntity.beginAt.lt(LocalTime.from(endDate)),
            todoEntity.userId.eq(userId)
        )
        .fetch();
  }

  @Override
  public List<TodoCompletionResponse> findTodosCompletion(
      LocalDate startDate,
      LocalDate endDate,
      Long userId,
      Long goalId,
      List<PrivacyType> privacyTypes,
      boolean isAchieved
  ) {
    BooleanBuilder condition = new BooleanBuilder(todoEntity.userId.eq(userId));

    if (Objects.nonNull(goalId)) {
      condition.and(todoEntity.goalId.eq(goalId));
    }

    if (!isAchieved) {
      condition.and(todoEntity.postponedAt.isNotNull())
          .and(
              todoEntity.postponedAt.between(
                  startDate.atStartOfDay(),
                  endDate.atTime(LocalTime.MAX)
              )
          );
    }

    condition.and(privacyTypesIn(privacyTypes));

    if (isAchieved) {
      condition.and(todoEntity.scheduledOn.between(startDate, endDate));
    }

    return jpaQueryFactory
        .select(projectCompletion())
        .from(todoEntity)
        .join(goalEntity)
        .on(todoEntity.goalId.eq(goalEntity.id))
        .where(condition)
        .groupBy(todoEntity.scheduledOn)
        .orderBy(todoEntity.scheduledOn.asc())
        .fetch();
  }

  @Override
  public List<TodoCursorDto> findScrollTodos(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine,
      Boolean isFollower
  ) {
    BooleanExpression cursorFilter = getCursorFilter(request.getOrder(), request.getCursor());
    Predicate openness = getOpenness(isMine, isFollower);
    BooleanBuilder condition = new BooleanBuilder(cursorFilter)
        .and(todoEntity.userId.eq(userId))
        .and(openness);

    if (StringUtils.isNotBlank(query)) {
      condition.and(todoEntity.name.containsIgnoreCase(query));
    }

    OrderSpecifier<?> order = decideOrder(request.getOrder());

    return jpaQueryFactory.select(projectTodoCursor(request.getOrder()))
        .from(todoEntity)
        .where(condition)
        .orderBy(order, todoEntity.id.desc())
        .limit(request.getSize() + 1)
        .fetch();
  }

  @Override
  public List<TodoEntity> findAllByDateAndUserAndPrivacyTypes(
      LocalDate date,
      Long userId,
      List<PrivacyType> accessiblePrivacyTypes
  ) {
    return jpaQueryFactory
        .selectFrom(todoEntity)
        .join(goalEntity)
        .on(todoEntity.goalId.eq(goalEntity.id))
        .where(
            scheduledOnEq(date),
            todoEntity.userId.eq(userId),
            privacyTypesIn(accessiblePrivacyTypes)
        )
        .fetch();
  }

  @Override
  public void deleteAllByGoalId(Long goalId) {
    jpaQueryFactory
        .delete(todoEntity)
        .where(todoEntity.goalId.eq(goalId))
        .execute();

    entityManager.flush();
    entityManager.clear();
  }

  @Override
  public void deleteAllByRepeatTodoId(Long repeatTodoId) {
    jpaQueryFactory
        .delete(todoEntity)
        .where(
            todoEntity.repeatTodoId.eq(repeatTodoId),
            todoEntity.status.eq(TodoStatus.UNCOMPLETED)
        )
        .execute();

    entityManager.flush();
    entityManager.clear();
  }

  @Override
  public List<BaseStats> findStatsBaseOfUser(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  ) {
    BooleanBuilder condition = new BooleanBuilder(goalEntity.userId.eq(userId))
        .and(todoEntity.scheduledOn.between(from, to));

    if (Objects.nonNull(goalId)) {
      condition.and(todoEntity.goalId.eq(goalId));
    }

    return jpaQueryFactory
        .select(projectionStatsBase())
        .from(todoEntity)
        .join(goalEntity)
        .on(todoEntity.goalId.eq(goalEntity.id))
        .where(condition)
        .orderBy(
            todoEntity.scheduledOn.yearMonth()
                .asc(), todoEntity.scheduledOn.asc(), todoEntity.status.asc()
        )
        .fetch();
  }

  @Override
  public List<BaseStats> findPostponedStatsBaseOfUser(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  ) {
    BooleanBuilder condition = new BooleanBuilder(goalEntity.userId.eq(userId))
        .and(todoEntity.postponedAt.isNotNull())
        .and(todoEntity.postponedAt.between(from.atStartOfDay(), to.atTime(LocalTime.MAX)));

    if (Objects.nonNull(goalId)) {
      condition.and(todoEntity.goalId.eq(goalId));
    }

    return jpaQueryFactory
        .select(projectionStatsBase())
        .from(todoEntity)
        .join(goalEntity)
        .on(todoEntity.goalId.eq(goalEntity.id))
        .where(condition)
        .orderBy(
            todoEntity.scheduledOn.yearMonth()
                .asc(), todoEntity.scheduledOn.asc(), todoEntity.status.asc()
        )
        .fetch();
  }

  @Override
  public List<RepeatTodoStatsDto> countByRepeatTodoId(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  ) {
    NumberExpression<Integer> totalCount = todoEntity.count()
        .intValue();
    NumberTemplate<Integer> completedCount = Expressions.numberTemplate(
        Integer.class,
        "SUM(CASE WHEN {0} = {1} THEN 1 ELSE 0 END)",
        todoEntity.status,
        TodoStatus.COMPLETE
    );
    BooleanBuilder condition = new BooleanBuilder(todoEntity.goalId.eq(goalId))
        .and(todoEntity.userId.eq(userId))
        .and(todoEntity.scheduledOn.between(from, to));

    return jpaQueryFactory
        .select(projectRepeatTodoStats(completedCount, totalCount))
        .from(todoEntity)
        .join(repeatTodoEntity)
        .on(repeatTodoEntity.id.eq(todoEntity.repeatTodoId))
        .where(condition)
        .groupBy(todoEntity.repeatTodoId)
        .orderBy(completedCount.desc(), totalCount.desc())
        .fetch();
  }


  @Override
  public List<GoalStatusSummaryRaw> findGoalStatuses(Long userId, Long goalId) {
    return jpaQueryFactory
        .select(projectGoalStatusSummary())
        .from(todoEntity)
        .join(goalEntity)
        .on(todoEntity.goalId.eq(goalEntity.id))
        .where(
            todoEntity.userId.eq(userId),
            todoEntity.goalId.eq(goalId),
            goalEntity.userId.eq(userId)
        )
        .fetch();
  }

  @Override
  public int countTodayByUserId(Long userId) {
    return jpaQueryFactory.select(Wildcard.countAsInt)
        .from(todoEntity)
        .where(todoEntity.userId.eq(userId), todoEntity.scheduledOn.eq(LocalDate.now()))
        .fetchOne();
  }


  @Override
  public List<TodoEntity> findAllByUserId(Long userId) {
    return jpaQueryFactory
        .selectFrom(todoEntity)
        .where(todoEntity.userId.eq(userId))
        .fetch();
  }
  private Predicate getOpenness(boolean isMine, boolean isFollower) {
    EnumPath<PrivacyType> privacyType = goalEntity.privacyType;

    if (isMine) {
      return null;
    }

    if (isFollower) {
      return privacyType.in(PrivacyType.FOLLOWER, PrivacyType.PRIVATE);
    }

    return privacyType.in(PrivacyType.PUBLIC);
  }

  private BooleanExpression getCursorFilter(OrderType orderType, String cursor) {
    if (StringUtils.isBlank(cursor)) {
      return null;
    }

    validateOrderType(orderType);

    long idCursor = Long.parseLong(cursor);

    return idCursor > 0 ? todoEntity.id.lt(idCursor) : null;
  }

  private OrderSpecifier<?> decideOrder(OrderType orderType) {
    validateOrderType(orderType);

    return new OrderSpecifier<>(Order.ASC, Expressions.nullExpression());
  }

  private ConstructorExpression<TodoCursorDto> projectTodoCursor(OrderType orderType) {
    StringExpression cursor = getCursor(orderType);

    return Projections.constructor(TodoCursorDto.class, cursor, projectSimpleTodo());
  }

  private ConstructorExpression<SimpleTodoSearchDto> projectSimpleTodo() {
    return Projections.constructor(
        SimpleTodoSearchDto.class,
        todoEntity.id,
        todoEntity.name,
        todoEntity.scheduledOn,
        todoEntity.postponedAt
    );
  }

  private StringExpression getCursor(OrderType orderType) {
    validateOrderType(orderType);

    return todoEntity.id.stringValue();
  }

  private void validateOrderType(OrderType orderType) {
    if (Objects.isNull(orderType) || !orderType.isLatest()) {
      throw new NotImplementedException("아직 구현되지 않은 검색 결과 순서입니다.");
    }
  }


  private ConstructorExpression<GoalStatusSummaryRaw> projectGoalStatusSummary() {
    return Projections.constructor(
        GoalStatusSummaryRaw.class,
        todoEntity.id,
        todoEntity.status
    );
  }

  private BooleanExpression privacyTypesIn(List<PrivacyType> accessiblePrivacyTypes) {
    return goalEntity.privacyType.in(accessiblePrivacyTypes);
  }

  private BooleanExpression scheduledOnEq(LocalDate date) {
    return todoEntity.scheduledOn.eq(date);
  }

  private ConstructorExpression<TodoCompletionResponse> projectCompletion() {
    NumberTemplate<Integer> totalTodosTemplate = Expressions.numberTemplate(
        Integer.class,
        "COUNT({0})",
        todoEntity.id
    );
    NumberTemplate<Integer> completedTodosTemplate = Expressions.numberTemplate(
        Integer.class,
        "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
        todoEntity.status,
        TodoStatus.COMPLETE,
        todoEntity.id
    );
    NumberTemplate<Integer> uncompletedTodosTemplate = Expressions.numberTemplate(
        Integer.class,
        "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
        todoEntity.status,
        TodoStatus.UNCOMPLETED,
        todoEntity.id
    );

    return Projections.constructor(
        TodoCompletionResponse.class,
        todoEntity.scheduledOn,
        totalTodosTemplate,
        completedTodosTemplate,
        uncompletedTodosTemplate
    );
  }

  private ConstructorExpression<BaseStats> projectionStatsBase() {
    Expression<com.modoo.aggregate.enums.TodoStatus> status = ExpressionUtils.as(
        todoEntity.status.when(TodoStatus.COMPLETE)
            .then(com.modoo.aggregate.enums.TodoStatus.COMPLETE)
            .otherwise(com.modoo.aggregate.enums.TodoStatus.UNCOMPLETED),
        "status"
    );

    return Projections.constructor(
        BaseStats.class,
        todoEntity.id.as("todoId"),
        goalEntity.id,
        goalEntity.name,
        goalEntity.color.stringValue(),
        status,
        todoEntity.postponedAt.isNotNull(),
        todoEntity.scheduledOn,
        todoEntity.beginAt,
        todoEntity.endAt
    );
  }

  private ConstructorExpression<RepeatTodoStatsDto> projectRepeatTodoStats(
      NumberExpression<Integer> completedCount,
      NumberExpression<Integer> totalCount
  ) {
    return Projections.constructor(
        RepeatTodoStatsDto.class,
        todoEntity.repeatTodoId,
        repeatTodoEntity.name,
        completedCount,
        totalCount
    );
  }

}
