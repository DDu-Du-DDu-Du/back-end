package com.modoo.api.planning.reminder.controller;

import com.modoo.api.planning.reminder.doc.ReminderControllerDoc;
import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.reminder.request.CreateReminderRequest;
import com.modoo.application.common.dto.reminder.request.UpdateReminderRequest;
import com.modoo.application.common.dto.reminder.response.CreateReminderResponse;
import com.modoo.application.common.dto.reminder.response.RetrieveReminderResponse;
import com.modoo.application.common.port.reminder.in.CancelReminderByIdUseCase;
import com.modoo.application.common.port.reminder.in.CreateReminderUseCase;
import com.modoo.application.common.port.reminder.in.RetrieveRemindersUseCase;
import com.modoo.application.common.port.reminder.in.UpdateReminderUseCase;
import com.modoo.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController implements ReminderControllerDoc {

  private final CreateReminderUseCase createReminderUseCase;
  private final RetrieveRemindersUseCase retrieveRemindersUseCase;
  private final CancelReminderByIdUseCase cancelReminderByIdUseCase;
  private final UpdateReminderUseCase updateReminderUseCase;

  @Override
  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateReminderRequest request
  ) {
    CreateReminderResponse response = createReminderUseCase.create(loginId, request);
    URI uri = URI.create("/api/reminders/" + response.id());

    return ResponseEntity.created(uri)
        .body(new IdResponse(response.id()));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<RetrieveReminderResponse>> retrieve(
      @Login
      Long loginId,
      @RequestParam
      Long todoId
  ) {
    List<RetrieveReminderResponse> response = retrieveRemindersUseCase.retrieve(loginId, todoId);

    return ResponseEntity.ok(response);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancel(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    cancelReminderByIdUseCase.cancel(loginId, id);

    return ResponseEntity.noContent()
        .build();
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<Void> update(
      @Login
      Long loginId,
      @PathVariable
      Long id,
      @RequestBody
      @Valid
      UpdateReminderRequest request
  ) {
    updateReminderUseCase.update(loginId, id, request);

    return ResponseEntity.noContent()
        .build();
  }


}
