package com.ddudu.infrastructure.persistence.repository.ddudu;

import static com.ddudu.infrastructure.persistence.entity.QDduduEntity.dduduEntity;
import static java.util.Objects.isNull;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.dto.ddudu.BasicDduduWithGoalId;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.ddudu.TimeGroupedDdudus;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.dto.goal.response.BasicGoalResponse;
import com.ddudu.application.dto.scroll.OrderType;
import com.ddudu.application.dto.scroll.request.ScrollRequest;
import com.ddudu.infrastructure.persistence.dto.DduduCursorDto;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DduduQueryRepositoryImpl implements DduduQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  @Override
  public List<DduduEntity> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user
  ) {
    return jpaQueryFactory
        .selectFrom(dduduEntity)
        .join(dduduEntity.goal)
        .fetchJoin()
        .where(
            dduduEntity.beginAt.goe(LocalTime.from(startDate)),
            dduduEntity.beginAt.lt(LocalTime.from(endDate)),
            dduduEntity.user.eq(user)
        )
        .fetch();
  }

  @Override
  public List<DduduEntity> findDdudusByDateAndUserAndGoals(
      LocalDate date, UserEntity user, List<GoalEntity> goals
  ) {
    return jpaQueryFactory
        .selectFrom(dduduEntity)
        .where(
            dduduEntity.scheduledOn.eq(date),
            dduduEntity.user.eq(user),
            dduduEntity.goal.in(goals)
        )
        .orderBy(dduduEntity.status.desc(), dduduEntity.createdAt.desc())
        .fetch();
  }

  @Override
  public List<DduduCompletionResponse> findDdudusCompletion(
      LocalDate startDate, LocalDate endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  ) {
    DateTemplate<LocalDate> dateTemplate = Expressions.dateTemplate(
        LocalDate.class, "{0}", dduduEntity.scheduledOn);

    NumberTemplate<Long> totalTodosTemplate = Expressions.numberTemplate(
        Long.class,
        "COUNT({0})", dduduEntity.id
    );

    NumberTemplate<Long> uncompletedTodosTemplate = Expressions.numberTemplate(
        Long.class,
        "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
        dduduEntity.status,
        DduduStatus.UNCOMPLETED,
        dduduEntity.id
    );

    return jpaQueryFactory
        .select(
            dateTemplate
                .as("date"),
            totalTodosTemplate
                .as("totalTodos"),
            uncompletedTodosTemplate
                .as("uncompletedTodos")
        )
        .from(dduduEntity)
        .join(dduduEntity.goal)
        .on(
            dduduEntity.goal.user.eq(user),
            dduduEntity.goal.privacyType.in(privacyTypes)
        )
        .where(
            dduduEntity.scheduledOn.goe(LocalDate.from(startDate)),
            dduduEntity.scheduledOn.lt(LocalDate.from(endDate))
        )
        .groupBy(dateTemplate)
        .fetch()
        .stream()
        .map(result -> DduduCompletionResponse.builder()
            .date(Objects.requireNonNull(result.get(0, LocalDate.class)))
            .totalCount(Objects.requireNonNull(result.get(1, Long.class))
                .intValue())
            .uncompletedCount(Objects.requireNonNull(result.get(2, Long.class))
                .intValue())
            .build())
        .toList();
  }

  @Override
  public void deleteAllByGoal(GoalEntity goal) {
    jpaQueryFactory
        .delete(dduduEntity)
        .where(dduduEntity.goal.eq(goal))
        .execute();

    entityManager.clear();
  }

  @Override
  public List<GoalGroupedDdudus> findDailyDdudusByUserGroupByGoal(
      LocalDate date, UserEntity user, List<GoalEntity> goals
  ) {
    List<DduduEntity> ddudus = fetchDdudusByUserAndGoals(date, user, goals, null);
    return mapToGoalGroupedDdudus(goals, ddudus);
  }

  @Override
  public List<GoalGroupedDdudus> findUnassignedDdudusByUserGroupByGoal(
      LocalDate date, UserEntity user, List<GoalEntity> goals
  ) {
    List<DduduEntity> ddudus = fetchDdudusByUserAndGoals(date, user, goals, true);
    return mapToGoalGroupedDdudus(goals, ddudus);
  }

  @Override
  public List<TimeGroupedDdudus> findDailyDdudusByUserGroupByTime(
      LocalDate date, UserEntity user, List<GoalEntity> goals
  ) {
    List<DduduEntity> ddudus = fetchDdudusByUserAndGoals(date, user, goals, false);
    return mapToTimeGroupedDdudus(ddudus);
  }

  private List<DduduEntity> fetchDdudusByUserAndGoals(
      LocalDate date, UserEntity user, List<GoalEntity> goals, Boolean unassigned
  ) {
    BooleanBuilder whereClause = new BooleanBuilder()
        .and(dduduEntity.user.eq(user))
        .and(dduduEntity.goal.in(goals))
        .and(dduduEntity.scheduledOn.eq(date));

    if (isNull(unassigned)) {
      return jpaQueryFactory
          .selectFrom(dduduEntity)
          .where(whereClause)
          .orderBy(dduduEntity.goal.id.asc(), dduduEntity.status.desc(), dduduEntity.id.desc())
          .fetch();
    }

    if (unassigned) {
      whereClause.and(dduduEntity.beginAt.isNull()
          .or(dduduEntity.endAt.isNull()));
    } else {
      whereClause.and(dduduEntity.beginAt.isNotNull()
          .and(dduduEntity.endAt.isNotNull()));
    }

    return jpaQueryFactory
        .selectFrom(dduduEntity)
        .where(whereClause)
        .orderBy(dduduEntity.status.desc(), dduduEntity.id.desc())
        .fetch();
  }

  private List<GoalGroupedDdudus> mapToGoalGroupedDdudus(
      List<GoalEntity> goals, List<DduduEntity> ddudus
  ) {
    Map<Long, List<DduduEntity>> ddudusByGoalId = ddudus.stream()
        .collect(Collectors.groupingBy(ddudu -> ddudu.getGoal()
            .getId()));

    return goals.stream()
        .sorted(Comparator.comparing(GoalEntity::getId))
        .map(goal -> GoalGroupedDdudus.builder()
            .goal(BasicGoalResponse.from(goal.toDomain()))
            .ddudus(ddudusByGoalId.getOrDefault(goal.getId(), List.of())
                .stream()
                .map(ddudu -> BasicDduduResponse.from(ddudu.toDomain()))
                .toList()
            )
            .build()
        )
        .toList();
  }

  private List<TimeGroupedDdudus> mapToTimeGroupedDdudus(
      List<DduduEntity> ddudus
  ) {
    List<LocalTime> times = extractDistinctSortedTimes(ddudus);
    Map<LocalTime, List<DduduEntity>> ddudusByTime = ddudus.stream()
        .collect(Collectors.groupingBy(DduduEntity::getBeginAt));

    return times.stream()
        .map(time -> TimeGroupedDdudus.of(time, ddudusByTime.getOrDefault(time, List.of())
            .stream()
            .map(ddudu -> BasicDduduWithGoalId.of(ddudu.toDomain()))
            .toList()
        ))
        .toList();
  }

  private List<LocalTime> extractDistinctSortedTimes(List<DduduEntity> ddudus) {
    return ddudus.stream()
        .map(DduduEntity::getBeginAt)
        .filter(Objects::nonNull)
        .distinct()
        .sorted()
        .toList();
  }

  public List<DduduCursorDto> findScrollDdudus(
      Long userId, ScrollRequest request, String query, Boolean isMine, Boolean isFollower
  ) {
    BooleanExpression cursorFilter = getCursorFilter(request.getOrder(), request.getCursor());
    Predicate openness = getOpenness(isMine, isFollower);
    BooleanBuilder condition = new BooleanBuilder(cursorFilter)
        .and(dduduEntity.user.id.eq(userId))
        .and(openness);

    if (StringUtils.isNotBlank(query)) {
      condition.and(dduduEntity.name.containsIgnoreCase(query));
    }

    OrderSpecifier<?> order = decideOrder(request.getOrder());

    return jpaQueryFactory.select(projectDduduCursor(request.getOrder()))
        .from(dduduEntity)
        .where(condition)
        .orderBy(order, dduduEntity.id.desc())
        .limit(request.getSize() + 1)
        .fetch();
  }

  private Predicate getOpenness(boolean isMine, boolean isFollower) {
    EnumPath<PrivacyType> privacyType = dduduEntity.goal.privacyType;

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

    return idCursor > 0 ? dduduEntity.id.lt(idCursor) : null;
  }

  private OrderSpecifier<?> decideOrder(OrderType orderType) {
    validateOrderType(orderType);

    return new OrderSpecifier<>(Order.ASC, Expressions.nullExpression());
  }

  private ConstructorExpression<DduduCursorDto> projectDduduCursor(OrderType orderType) {
    StringExpression cursor = getCursor(orderType);

    return Projections.constructor(
        DduduCursorDto.class, cursor, projectSimpleDdudu());
  }

  private ConstructorExpression<SimpleDduduSearchDto> projectSimpleDdudu() {
    return Projections.constructor(
        SimpleDduduSearchDto.class, dduduEntity.id, dduduEntity.name, dduduEntity.scheduledOn);
  }

  private StringExpression getCursor(OrderType orderType) {
    validateOrderType(orderType);

    return dduduEntity.id.stringValue();
  }

  private void validateOrderType(OrderType orderType) {
    if (Objects.isNull(orderType) || !orderType.isLatest()) {
      throw new NotImplementedException("아직 구현되지 않은 검색 결과 순서입니다.");
    }
  }

}
