package com.ddudu.infrastructure.persistence.repository.ddudu;

import static com.ddudu.infrastructure.persistence.entity.QDduduEntity.dduduEntity;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.DduduEntity;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DduduQueryRepositoryImpl implements DduduQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public DduduQueryRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<DduduEntity> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user
  ) {
    return jpaQueryFactory
        .selectFrom(dduduEntity)
        .join(dduduEntity.goal)
        .fetchJoin()
        .where(
            dduduEntity.beginAt.between(startDate, endDate),
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
            dduduEntity.beginAt.goe(startDate),
            dduduEntity.beginAt.lt(endDate),
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
  }

}
