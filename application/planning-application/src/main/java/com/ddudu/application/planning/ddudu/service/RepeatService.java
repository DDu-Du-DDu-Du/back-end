package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.ddudu.response.RepeatAnotherDayResponse;
import com.ddudu.application.common.port.ddudu.in.RepeatUseCase;
import com.ddudu.application.common.port.ddudu.out.RepeatDduduPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class RepeatService implements RepeatUseCase {

  private final RepeatDduduPort repeatDduduPort;

  @Override
  public RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId,
      Long dduduId,
      RepeatAnotherDayRequest request
  ) {
    Ddudu ddudu = repeatDduduPort.getDduduOrElseThrow(
        dduduId,
        DduduErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateDduduCreator(loginId);

    Ddudu replica = ddudu.reproduceOnDate(request.repeatOn());
    Ddudu repeatedDdudu = repeatDduduPort.save(replica);

    return new RepeatAnotherDayResponse(repeatedDdudu.getId());
  }

}
