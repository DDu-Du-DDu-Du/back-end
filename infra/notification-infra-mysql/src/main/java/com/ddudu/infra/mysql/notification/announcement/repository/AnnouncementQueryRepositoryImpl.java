package com.ddudu.infra.mysql.notification.announcement.repository;

import static com.ddudu.infra.mysql.notification.announcement.entity.QAnnouncementEntity.announcementEntity;
import static com.ddudu.infra.mysql.user.user.entity.QUserEntity.userEntity;

import com.ddudu.application.common.dto.notification.AnnouncementCursorDto;
import com.ddudu.application.common.dto.notification.SimpleAnnouncementDto;
import com.ddudu.application.common.dto.scroll.OrderType;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnnouncementQueryRepositoryImpl implements AnnouncementQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<AnnouncementCursorDto> findAnnouncementScroll(ScrollRequest request) {
    BooleanExpression cursorFilter = getCursorFilter(request.getOrder(), request.getCursor());
    BooleanBuilder condition = new BooleanBuilder(cursorFilter);

    return jpaQueryFactory.select(projectAnnouncementCursor(request.getOrder()))
        .from(announcementEntity)
        .join(userEntity)
        .on(announcementEntity.userId.eq(userEntity.id))
        .where(condition)
        .orderBy(announcementEntity.id.desc())
        .limit(request.getSize() + 1L)
        .fetch();
  }

  private BooleanExpression getCursorFilter(OrderType orderType, String cursor) {
    if (StringUtils.isBlank(cursor)) {
      return null;
    }

    validateOrderType(orderType);

    long idCursor = Long.parseLong(cursor);

    return idCursor > 0 ? announcementEntity.id.lt(idCursor) : null;
  }

  private ConstructorExpression<AnnouncementCursorDto> projectAnnouncementCursor(
      OrderType orderType
  ) {
    StringExpression cursor = getCursor(orderType);

    return Projections.constructor(
        AnnouncementCursorDto.class,
        cursor,
        projectSimpleAnnouncement()
    );
  }

  private ConstructorExpression<SimpleAnnouncementDto> projectSimpleAnnouncement() {
    return Projections.constructor(
        SimpleAnnouncementDto.class,
        announcementEntity.id,
        announcementEntity.title,
        userEntity.nickname,
        announcementEntity.createdAt
    );
  }

  private StringExpression getCursor(OrderType orderType) {
    validateOrderType(orderType);

    return announcementEntity.id.stringValue();
  }

  private void validateOrderType(OrderType orderType) {
    if (Objects.isNull(orderType) || !orderType.isLatest()) {
      throw new NotImplementedException("아직 구현되지 않은 검색 결과 순서입니다.");
    }
  }

}
