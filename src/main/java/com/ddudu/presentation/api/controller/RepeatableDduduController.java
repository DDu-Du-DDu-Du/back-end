package com.ddudu.presentation.api.controller;

import com.ddudu.application.dto.repeatable_ddudu.requset.CreateRepeatableDduduRequest;
import com.ddudu.application.port.in.repeatable_ddudu.CreateRepeatableDduduUseCase;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import com.ddudu.presentation.api.doc.RepeatableDduduControllerDoc;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repeatable-ddudus")
@RequiredArgsConstructor
public class RepeatableDduduController implements RepeatableDduduControllerDoc {

  private final CreateRepeatableDduduUseCase createRepeatableDduduUseCase;

  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateRepeatableDduduRequest request
  ) {
    Long id = createRepeatableDduduUseCase.create(loginId, request);
    URI uri = URI.create("/api/repeatable-ddudus/" + id);

    return ResponseEntity.created(uri)
        .body(new IdResponse(id));
  }

}
