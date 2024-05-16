package com.ddudu.infrastructure.persistence.repository.ddudu;

import static com.ddudu.old.persistence.entity.QTodoEntity.todoEntity;

import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
import com.ddudu.old.persistence.entity.TodoEntity;
import com.ddudu.old.todo.domain.TodoStatus;
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
  public List<TodoEntity> findTodosByDate(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user
  ) {
    return jpaQueryFactory
        .selectFrom(todoEntity)
        .join(todoEntity.goal)
        .fetchJoin()
        .where(
            todoEntity.beginAt.between(startDate, endDate),
            todoEntity.user.eq(user)
        )
        .orderBy(todoEntity.status.desc(), todoEntity.endAt.asc())
        .fetch();
  }

  @Override
  public List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, UserEntity user,
      List<PrivacyType> privacyTypes
  ) {
    DateTemplate<LocalDate> dateTemplate = Expressions.dateTemplate(
        LocalDate.class, "{0}", todoEntity.beginAt);

    NumberTemplate<Long> totalTodosTemplate = Expressions.numberTemplate(
        Long.class,
        "COUNT({0})", todoEntity.id
    );

    NumberTemplate<Long> uncompletedTodosTemplate = Expressions.numberTemplate(
        Long.class,
        "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
        todoEntity.status,
        TodoStatus.UNCOMPLETED,
        todoEntity.id
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
        .from(todoEntity)
        .where(
            todoEntity.beginAt.goe(startDate),
            todoEntity.beginAt.lt(endDate),
            todoEntity.user.eq(user),
            todoEntity.goal.privacyType.in(privacyTypes)
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

}
