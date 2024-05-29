package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.port.in.ddudu.CreateDduduUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateDduduService implements CreateDduduUseCase {

  @Override
  public BasicDduduResponse create(Long loginId, CreateDduduRequest request) {
    return null;
  }

}
