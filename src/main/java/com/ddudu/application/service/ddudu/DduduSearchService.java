package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;
import com.ddudu.application.dto.ddudu.request.DduduSearchRequest;
import com.ddudu.application.dto.scroll.response.ScrollResponse;
import com.ddudu.application.port.in.ddudu.DduduSearchUseCase;
import com.ddudu.application.port.out.ddudu.DduduSearchPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
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
