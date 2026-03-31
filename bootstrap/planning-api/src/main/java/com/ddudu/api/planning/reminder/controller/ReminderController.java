package com.ddudu.api.planning.reminder.controller;

import com.ddudu.api.planning.reminder.doc.ReminderControllerDoc;
import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.reminder.request.CreateReminderRequest;
import com.ddudu.application.common.dto.reminder.response.CreateReminderResponse;
import com.ddudu.application.common.port.reminder.in.CancelReminderByIdUseCase;
import com.ddudu.application.common.port.reminder.in.CreateReminderUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController implements ReminderControllerDoc {

  private final CreateReminderUseCase createReminderUseCase;
  private final CancelReminderByIdUseCase cancelReminderByIdUseCase;

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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancel(
      @Login
      Long loginId,
      @PathVariable
      Long id
  ) {
    cancelReminderByIdUseCase.cancel(loginId, id);

    return ResponseEntity.noContent().build();
  }

}
