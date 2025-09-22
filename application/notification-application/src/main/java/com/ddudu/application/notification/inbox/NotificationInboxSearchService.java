package com.ddudu.application.notification.inbox;

import com.ddudu.application.common.dto.notification.NotificationInboxCursorDto;
import com.ddudu.application.common.dto.notification.NotificationInboxSearchDto;
import com.ddudu.application.common.dto.notification.request.NotificationInboxSearchRequest;
import com.ddudu.application.common.dto.notification.response.NotificationInboxSearchResponse;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.notification.in.NotificationInboxSearchUseCase;
import com.ddudu.application.common.port.notification.out.NotificationInboxLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.NotificationInboxErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class NotificationInboxSearchService implements NotificationInboxSearchUseCase {

  private final UserLoaderPort userLoaderPort;
  private final NotificationInboxLoaderPort notificationInboxLoaderPort;

  @Override
  public ScrollResponse<NotificationInboxSearchResponse> search(
      Long loginId,
      NotificationInboxSearchRequest request
  ) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        NotificationInboxErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    ScrollRequest scrollRequest = request.getScroll();
    List<NotificationInboxCursorDto> inboxesWithCursor = notificationInboxLoaderPort.search(
        user.getId(),
        scrollRequest
    );

    // TODO: 이후 템플릿 구현 등에 따라 보낸 사람 이름이 필요할 수 있음

    return getScrollResponse(user.getId(), inboxesWithCursor, scrollRequest.getSize());
  }

  private ScrollResponse<NotificationInboxSearchResponse> getScrollResponse(
      Long userId,
      List<NotificationInboxCursorDto> inboxesWithCursor,
      int size
  ) {
    List<NotificationInboxSearchResponse> inboxes = inboxesWithCursor.stream()
        .limit(size)
        .map(withCursor -> mapCursorResultToSearchResponse(userId, withCursor))
        .toList();
    String nextCursor = getNextCursor(inboxesWithCursor, size);

    return ScrollResponse.from(inboxes, nextCursor);
  }

  private String getNextCursor(List<NotificationInboxCursorDto> inboxesWithCursor, int size) {
    if (inboxesWithCursor.size() > size) {
      return inboxesWithCursor.get(size - 1)
          .cursor();
    }

    return null;
  }

  private NotificationInboxSearchResponse mapCursorResultToSearchResponse(
      Long userId,
      NotificationInboxCursorDto withCursor
  ) {
    NotificationInboxSearchDto searchResult = withCursor.notificationInbox();

    return NotificationInboxSearchResponse.builder()
        .id(searchResult.id())
        .body(searchResult.body())
        .context(searchResult.typeCode()
            .getUpstreamContext())
        .contextId(searchResult.contextId())
        .createdAt(searchResult.createdAt())
        .isFromSystem(Objects.isNull(searchResult.senderId()) || Objects.equals(
            searchResult.senderId(),
            userId
        ))
        .title(searchResult.title())
        .isRead(Objects.nonNull(searchResult.readAt()))
        .senderId(searchResult.senderId())
        .build();
  }

}
