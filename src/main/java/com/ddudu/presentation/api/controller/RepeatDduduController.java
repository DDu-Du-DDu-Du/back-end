package com.ddudu.presentation.api.controller;

import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatDduduRequest;
import com.ddudu.application.dto.repeat_ddudu.request.UpdateRepeatDduduRequest;
import com.ddudu.application.port.in.repeat_ddudu.CreateRepeatDduduUseCase;
import com.ddudu.application.port.in.repeat_ddudu.DeleteRepeatDduduUseCase;
import com.ddudu.application.port.in.repeat_ddudu.UpdateRepeatDduduUseCase;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import com.ddudu.presentation.api.doc.RepeatDduduControllerDoc;
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
