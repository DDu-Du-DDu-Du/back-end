package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.dto.ddudu.response.RepeatAnotherDayResponse;
import com.ddudu.application.port.in.ddudu.RepeatUseCase;
import com.ddudu.application.port.out.ddudu.RepeatDduduPort;
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
