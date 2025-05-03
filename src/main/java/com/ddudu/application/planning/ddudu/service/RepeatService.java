package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.application.planning.ddudu.dto.request.RepeatAnotherDayRequest;
import com.ddudu.application.planning.ddudu.dto.response.RepeatAnotherDayResponse;
import com.ddudu.application.planning.ddudu.port.in.RepeatUseCase;
import com.ddudu.application.planning.ddudu.port.out.RepeatDduduPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class RepeatService implements RepeatUseCase {

  private final RepeatDduduPort repeatDduduPort;

  @Override
  public RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId, Long dduduId, RepeatAnotherDayRequest request
  ) {
    Ddudu ddudu = repeatDduduPort.getDduduOrElseThrow(
        dduduId, DduduErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.validateDduduCreator(loginId);

    Ddudu replica = ddudu.reproduceOnDate(request.repeatOn());
    Ddudu repeatedDdudu = repeatDduduPort.save(replica);

    return new RepeatAnotherDayResponse(repeatedDdudu.getId());
  }

}
