package com.ddudu.infrastructure.persistence.repository.ddudu;

import static com.ddudu.infrastructure.persistence.entity.QDduduEntity.dduduEntity;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.dto.GoalGroupedDdudus;
import com.ddudu.application.domain.ddudu.dto.response.BasicDduduResponse;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.response.GoalInfo;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
  public List<GoalGroupedDdudus> findDailyDdudusByUserGroupByGoal(
      LocalDate date, UserEntity user, List<GoalEntity> goals
  ) {
    List<DduduEntity> ddudus = jpaQueryFactory
        .selectFrom(dduduEntity)
        .where(
            dduduEntity.user.eq(user),
            dduduEntity.goal.in(goals),
            dduduEntity.scheduledOn.eq(date)
        )
        .orderBy(dduduEntity.goal.id.asc(), dduduEntity.status.desc(), dduduEntity.id.desc())
        .fetch();

    Map<Long, List<DduduEntity>> ddudusByGoalId = ddudus.stream()
        .collect(Collectors.groupingBy(ddudu -> ddudu.getGoal()
            .getId()));

    return goals.stream()
        .map(goal -> GoalGroupedDdudus.builder()
            .goal(GoalInfo.from(goal.toDomain()))
            .ddudus(ddudusByGoalId.getOrDefault(goal.getId(), List.of())
                .stream()
                .map(ddudu -> BasicDduduResponse.from(ddudu.toDomain()))
                .toList()
            )
            .build()
        )
        .toList();
  }

}
