package com.ddudu.api.planning.repeatddudu.controller;

import com.ddudu.api.planning.repeatddudu.doc.RepeatDduduControllerDoc;
import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.repeatddudu.request.CreateRepeatDduduRequest;
import com.ddudu.application.common.dto.repeatddudu.request.UpdateRepeatDduduRequest;
import com.ddudu.application.common.port.repeatddudu.in.CreateRepeatDduduUseCase;
import com.ddudu.application.common.port.repeatddudu.in.DeleteRepeatDduduUseCase;
import com.ddudu.application.common.port.repeatddudu.in.UpdateRepeatDduduUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repeat-ddudus")
@RequiredArgsConstructor
public class RepeatDduduController implements RepeatDduduControllerDoc {

  private final CreateRepeatDduduUseCase createRepeatDduduUseCase;
  private final UpdateRepeatDduduUseCase updateRepeatDduduUseCase;
  private final DeleteRepeatDduduUseCase deleteRepeatDduduUseCase;

  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateRepeatDduduRequest request
  ) {
    Long id = createRepeatDduduUseCase.create(loginId, request);
    URI uri = URI.create("/api/repeat-ddudus/" + id);

    return ResponseEntity.created(uri)
        .body(new IdResponse(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<IdResponse> update(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateRepeatDduduRequest request
  ) {
    Long repeatDduduId = updateRepeatDduduUseCase.update(loginId, id, request);

    return ResponseEntity.ok(new IdResponse(repeatDduduId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    deleteRepeatDduduUseCase.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
