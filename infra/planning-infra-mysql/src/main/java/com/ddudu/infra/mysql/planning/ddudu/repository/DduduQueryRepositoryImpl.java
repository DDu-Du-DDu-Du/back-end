package com.ddudu.infra.mysql.planning.ddudu.repository;

import static com.ddudu.infra.mysql.planning.ddudu.entity.QDduduEntity.dduduEntity;
import static com.ddudu.infra.mysql.planning.goal.entity.QGoalEntity.goalEntity;

import com.ddudu.aggregate.BaseStats;
import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.scroll.OrderType;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.infra.mysql.planning.ddudu.dto.DduduCursorDto;
import com.ddudu.infra.mysql.planning.ddudu.entity.DduduEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
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
import java.util.List;
import java.util.Objects;
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
  public List<DduduEntity> findDdudusByDate(
      LocalDateTime startDate, LocalDateTime endDate, Long userId
  ) {
    return jpaQueryFactory
        .selectFrom(dduduEntity)
        .join(goalEntity)
        .on(dduduEntity.goalId.eq(goalEntity.id))
        .where(
            dduduEntity.beginAt.goe(LocalTime.from(startDate)),
            dduduEntity.beginAt.lt(LocalTime.from(endDate)),
            dduduEntity.userId.eq(userId)
        )
        .fetch();
  }

  @Override
  public List<DduduCompletionResponse> findDdudusCompletion(
      LocalDate startDate, LocalDate endDate, Long userId, List<PrivacyType> privacyTypes
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
        .join(goalEntity)
        .on(dduduEntity.goalId.eq(goalEntity.id))
        .where(
            dduduEntity.userId.eq(userId),
            privacyTypesIn(privacyTypes),
            dduduEntity.scheduledOn.goe(startDate),
            dduduEntity.scheduledOn.lt(endDate)
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

  public List<DduduCursorDto> findScrollDdudus(
      Long userId,
      ScrollRequest request,
      String query,
      Boolean isMine,
      Boolean isFollower
  ) {
    BooleanExpression cursorFilter = getCursorFilter(request.getOrder(), request.getCursor());
    Predicate openness = getOpenness(isMine, isFollower);
    BooleanBuilder condition = new BooleanBuilder(cursorFilter)
        .and(dduduEntity.userId.eq(userId))
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

  @Override
  public List<DduduEntity> findAllByDateAndUserAndPrivacyTypes(
      LocalDate date,
      Long userId,
      List<PrivacyType> accessiblePrivacyTypes
  ) {
    return jpaQueryFactory
        .selectFrom(dduduEntity)
        .join(goalEntity)
        .on(dduduEntity.goalId.eq(goalEntity.id))
        .where(
            scheduledOnEq(date),
            dduduEntity.userId.eq(userId),
            privacyTypesIn(accessiblePrivacyTypes)
        )
        .fetch();
  }

  @Override
  public void deleteAllByGoalId(Long goalId) {
    jpaQueryFactory
        .delete(dduduEntity)
        .where(dduduEntity.goalId.eq(goalId))
        .execute();

    entityManager.flush();
    entityManager.clear();
  }

  @Override
  public void deleteAllByRepeatDduduId(Long repeatDduduId) {
    jpaQueryFactory
        .delete(dduduEntity)
        .where(
            dduduEntity.repeatDduduId.eq(repeatDduduId),
            dduduEntity.status.eq(DduduStatus.UNCOMPLETED)
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
        .and(dduduEntity.scheduledOn.between(from, to));

    if (Objects.nonNull(goalId)) {
      condition.and(dduduEntity.goalId.eq(goalId));
    }

    return jpaQueryFactory
        .select(projectionStatsBase())
        .from(dduduEntity)
        .join(goalEntity)
        .on(dduduEntity.goalId.eq(goalEntity.id))
        .where(condition)
        .orderBy(
            dduduEntity.scheduledOn.yearMonth()
                .asc(), dduduEntity.scheduledOn.asc(), dduduEntity.status.asc()
        )
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

    return idCursor > 0 ? dduduEntity.id.lt(idCursor) : null;
  }

  private OrderSpecifier<?> decideOrder(OrderType orderType) {
    validateOrderType(orderType);

    return new OrderSpecifier<>(Order.ASC, Expressions.nullExpression());
  }

  private ConstructorExpression<DduduCursorDto> projectDduduCursor(OrderType orderType) {
    StringExpression cursor = getCursor(orderType);

    return Projections.constructor(DduduCursorDto.class, cursor, projectSimpleDdudu());
  }

  private ConstructorExpression<SimpleDduduSearchDto> projectSimpleDdudu() {
    return Projections.constructor(
        SimpleDduduSearchDto.class,
        dduduEntity.id,
        dduduEntity.name,
        dduduEntity.scheduledOn
    );
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

  private BooleanExpression privacyTypesIn(List<PrivacyType> accessiblePrivacyTypes) {
    return goalEntity.privacyType.in(accessiblePrivacyTypes);
  }

  private BooleanExpression scheduledOnEq(LocalDate date) {
    return dduduEntity.scheduledOn.eq(date);
  }

  private ConstructorExpression<BaseStats> projectionStatsBase() {
    Expression<com.ddudu.aggregate.enums.DduduStatus> status = ExpressionUtils.as(
        dduduEntity.status.when(DduduStatus.COMPLETE)
            .then(com.ddudu.aggregate.enums.DduduStatus.COMPLETE)
            .otherwise(com.ddudu.aggregate.enums.DduduStatus.UNCOMPLETED),
        "status"
    );

    return Projections.constructor(
        BaseStats.class,
        dduduEntity.id.as("dduduId"),
        goalEntity.id,
        goalEntity.name,
        status,
        dduduEntity.isPostponed,
        dduduEntity.scheduledOn
    );
  }

}
