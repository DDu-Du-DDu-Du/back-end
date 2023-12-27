package com.ddudu.todo.repository;

import com.ddudu.todo.domain.QTodo;
import com.ddudu.todo.domain.Todo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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
  public List<Todo> findTodosByDate(LocalDateTime startDate, LocalDateTime endDate) {
    QTodo todo = QTodo.todo;

    return jpaQueryFactory
        .selectFrom(todo)
        .join(todo.goal)
        .fetchJoin()
        .where(
            todo.beginAt.between(startDate, endDate),
            todo.isDeleted.eq(false)
        )
        .orderBy(todo.status.desc(), todo.endAt.asc())
        .fetch();
  }

}
