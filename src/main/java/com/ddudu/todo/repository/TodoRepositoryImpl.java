package com.ddudu.todo.repository;

import com.ddudu.todo.domain.QTodo;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.domain.TodoStatus;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.user.domain.User;
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
public class TodoRepositoryImpl implements TodoRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public TodoRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<Todo> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate, User user) {
    QTodo todo = QTodo.todo;

    return jpaQueryFactory
        .selectFrom(todo)
        .join(todo.goal)
        .fetchJoin()
        .where(
            todo.beginAt.between(startDate, endDate),
            todo.user.eq(user)
        )
        .orderBy(todo.status.desc(), todo.endAt.asc())
        .fetch();
  }

  @Override
  public List<TodoCompletionResponse> findTodosCompletion(
      LocalDateTime startDate, LocalDateTime endDate, User user
  ) {
    QTodo todo = QTodo.todo;

    DateTemplate<LocalDate> dateTemplate = Expressions.dateTemplate(
        LocalDate.class, "{0}", todo.beginAt);

    NumberTemplate<Long> totalTodosTemplate = Expressions.numberTemplate(
        Long.class,
        "COUNT({0})", todo.id
    );

    NumberTemplate<Long> uncompletedTodosTemplate = Expressions.numberTemplate(
        Long.class,
        "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
        todo.status,
        TodoStatus.UNCOMPLETED,
        todo.id
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
        .from(todo)
        .where(
            todo.beginAt.goe(startDate),
            todo.beginAt.lt(endDate),
            todo.user.eq(user)
        )
        .groupBy(dateTemplate)
        .fetch()
        .stream()
        .map(result -> TodoCompletionResponse.builder()
            .date(result.get(0, LocalDateTime.class)
                .toLocalDate())
            .totalTodos(result.get(1, Long.class)
                .intValue())
            .uncompletedTodos(result.get(2, Long.class)
                .intValue())
            .build())
        .toList();
  }

}
