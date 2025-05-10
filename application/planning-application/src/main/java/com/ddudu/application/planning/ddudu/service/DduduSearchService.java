package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.common.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.common.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.common.port.ddudu.in.DduduSearchUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduSearchPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
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
        loginId, DduduErrorCode.USER_NOT_EXISTING.getCodeName());

    return dduduSearchPort.search(
        user.getId(), request.getScroll(), request.getQuery(), request.getIsMine());
  }

}
