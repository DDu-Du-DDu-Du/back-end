package com.modoo.infra.mysql.notification.inbox.repository;

import static com.modoo.infra.mysql.notification.inbox.entity.QNotificationInboxEntity.notificationInboxEntity;

import com.modoo.application.common.dto.notification.NotificationInboxCursorDto;
import com.modoo.application.common.dto.notification.NotificationInboxSearchDto;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationInboxQueryRepositoryImpl implements NotificationInboxQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<NotificationInboxCursorDto> findInboxScroll(Long userId, ScrollRequest request) {
    BooleanExpression cursorFilter = getCursorFilter(request.getCursor());
    BooleanBuilder condition = new BooleanBuilder(notificationInboxEntity.userId.eq(userId))
        .and(cursorFilter);

    return jpaQueryFactory.select(projectInboxCursor())
        .from(notificationInboxEntity)
        .where(condition)
        .orderBy(notificationInboxEntity.id.desc())
        .fetch();
  }

  @Override
  public long countUnreadByUserId(Long userId) {
    Long unreadCount = jpaQueryFactory
        .select(notificationInboxEntity.count())
        .from(notificationInboxEntity)
        .where(
            notificationInboxEntity.userId.eq(userId),
            notificationInboxEntity.readAt.isNull()
        )
        .fetchOne();

    return unreadCount == null ? 0L : unreadCount;
  }

  private BooleanExpression getCursorFilter(String cursor) {
    if (StringUtils.isBlank(cursor)) {
      return null;
    }

    long idCursor = Long.parseLong(cursor);

    return idCursor > 0 ? notificationInboxEntity.id.lt(idCursor) : null;
  }

  private ConstructorExpression<NotificationInboxCursorDto> projectInboxCursor() {
    return Projections.constructor(
        NotificationInboxCursorDto.class,
        notificationInboxEntity.id.stringValue(),
        projectInboxSearch()
    );
  }

  private ConstructorExpression<NotificationInboxSearchDto> projectInboxSearch() {
    return Projections.constructor(
        NotificationInboxSearchDto.class,
        notificationInboxEntity.id,
        notificationInboxEntity.senderId,
        notificationInboxEntity.typeCode,
        notificationInboxEntity.contextId,
        notificationInboxEntity.readAt,
        notificationInboxEntity.createdAt,
        notificationInboxEntity.title,
        notificationInboxEntity.body
    );
  }

}
