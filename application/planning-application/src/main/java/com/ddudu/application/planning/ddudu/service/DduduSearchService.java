package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.DduduCursorDto;
import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.ddudu.in.DduduSearchUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduSearchPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DduduSearchService implements DduduSearchUseCase {

  private final UserLoaderPort userLoaderPort;
  private final DduduSearchPort dduduSearchPort;

  @Override
  public ScrollResponse<SimpleDduduSearchDto> search(Long loginId, DduduSearchRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        DduduErrorCode.USER_NOT_EXISTING.getCodeName()
    );
    List<DduduCursorDto> ddudusWithCursor = dduduSearchPort.search(
        user.getId(),
        request.getScroll(),
        request.getQuery(),
        request.getIsMine()
    );

    return getScrollResponse(ddudusWithCursor, request.getSize());
  }

  private ScrollResponse<SimpleDduduSearchDto> getScrollResponse(
      List<DduduCursorDto> ddudusWithCursor,
      int size
  ) {
    List<SimpleDduduSearchDto> simpleDdudus = ddudusWithCursor.stream()
        .limit(size)
        .map(DduduCursorDto::ddudu)
        .toList();
    String nextCursor = getNextCursor(ddudusWithCursor, size);

    return ScrollResponse.from(simpleDdudus, nextCursor);
  }

  private String getNextCursor(List<DduduCursorDto> ddudusWithCursor, int size) {
    if (ddudusWithCursor.size() > size) {
      return ddudusWithCursor.get(size - 1)
          .cursor();
    }

    return null;
  }

}
