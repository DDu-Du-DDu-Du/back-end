package com.modoo.api.planning.repeattodo.controller;

import com.modoo.api.planning.repeattodo.doc.RepeatTodoControllerDoc;
import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.repeattodo.request.CreateRepeatTodoRequest;
import com.modoo.application.common.dto.repeattodo.request.UpdateRepeatTodoRequest;
import com.modoo.application.common.port.repeattodo.in.CreateRepeatTodoUseCase;
import com.modoo.application.common.port.repeattodo.in.DeleteRepeatTodoUseCase;
import com.modoo.application.common.port.repeattodo.in.UpdateRepeatTodoUseCase;
import com.modoo.bootstrap.common.annotation.Login;
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
@RequestMapping("/api/repeat-todos")
@RequiredArgsConstructor
public class RepeatTodoController implements RepeatTodoControllerDoc {

  private final CreateRepeatTodoUseCase createRepeatTodoUseCase;
  private final UpdateRepeatTodoUseCase updateRepeatTodoUseCase;
  private final DeleteRepeatTodoUseCase deleteRepeatTodoUseCase;

  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateRepeatTodoRequest request
  ) {
    Long id = createRepeatTodoUseCase.create(loginId, request);
    URI uri = URI.create("/api/repeat-todos/" + id);

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
      UpdateRepeatTodoRequest request
  ) {
    Long repeatTodoId = updateRepeatTodoUseCase.update(loginId, id, request);

    return ResponseEntity.ok(new IdResponse(repeatTodoId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    deleteRepeatTodoUseCase.delete(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

}
