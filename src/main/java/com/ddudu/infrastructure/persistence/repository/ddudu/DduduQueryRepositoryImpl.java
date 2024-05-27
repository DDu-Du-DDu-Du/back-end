package com.ddudu.infrastructure.persistence.repository.ddudu;

import static com.ddudu.infrastructure.persistence.entity.QDduduEntity.dduduEntity;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.scroll.OrderType;
import com.ddudu.application.dto.scroll.request.ScrollRequest;
import com.ddudu.infrastructure.persistence.dto.DduduCursorDto;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
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
  public List<DduduEntity> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user
  ) {
    return jpaQueryFactory
        .selectFrom(dduduEntity)
        .join(dduduEntity.goal)
        .fetchJoin()
        .where(
            dduduEntity.beginAt.between(LocalTime.from(startDate), LocalTime.from(endDate)),
            dduduEntity.user.eq(user)
        )
        .orderBy(dduduEntity.status.desc(), dduduEntity.endAt.asc())
        .fetch();
  }

  @Override
  public List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  ) {
    DateTemplate<LocalDate> dateTemplate = Expressions.dateTemplate(
        LocalDate.class, "{0}", dduduEntity.beginAt);

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
        .where(
            dduduEntity.beginAt.goe(LocalTime.from(startDate)),
            dduduEntity.beginAt.lt(LocalTime.from(endDate)),
            dduduEntity.user.eq(user),
            dduduEntity.goal.privacyType.in(privacyTypes)
        )
        .groupBy(dateTemplate)
        .fetch()
        .stream()
        .map(result -> TodoCompletionResponse.builder()
            .date(result.get(0, LocalDateTime.class)
                .toLocalDate())
            .totalCount(result.get(1, Long.class)
                .intValue())
            .uncompletedCount(result.get(2, Long.class)
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
  public List<DduduCursorDto> findScrollMyDdudus(
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
