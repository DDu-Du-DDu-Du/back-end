package com.ddudu.infra.mysql.notification.event.repository;

import static com.ddudu.infra.mysql.notification.event.entity.QNotificationEventEntity.notificationEventEntity;

import com.ddudu.application.common.dto.notification.ReminderScheduleTargetDto;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationEventQueryRepositoryImpl implements NotificationEventQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Map<Long, List<ReminderScheduleTargetDto>> findAllDduduRemindersScheduledOn(LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1)
        .minusNanos(1);

    BooleanBuilder condition = new BooleanBuilder(
        notificationEventEntity.willFireAt.between(
            startOfDay,
            endOfDay
        )
    ).and(notificationEventEntity.firedAt.isNull())
        .and(notificationEventEntity.typeCode.eq(NotificationEventTypeCode.DDUDU_REMINDER));

    return jpaQueryFactory
        .from(notificationEventEntity)
        .where(condition)
        .transform(
            GroupBy.groupBy(notificationEventEntity.receiverId)
                .as(GroupBy.list(projectReminderScheduleTarget()))
        );
  }

  private ConstructorExpression<ReminderScheduleTargetDto> projectReminderScheduleTarget() {
    return Projections.constructor(
        ReminderScheduleTargetDto.class,
        notificationEventEntity.id,
        notificationEventEntity.willFireAt
    );
  }

}
