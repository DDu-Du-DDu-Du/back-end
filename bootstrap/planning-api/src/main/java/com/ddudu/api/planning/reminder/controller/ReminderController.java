package com.ddudu.api.planning.reminder.controller;

import com.ddudu.api.planning.reminder.doc.ReminderControllerDoc;
import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.reminder.request.CreateReminderRequest;
import com.ddudu.application.common.dto.reminder.request.UpdateReminderRequest;
import com.ddudu.application.common.dto.reminder.response.CreateReminderResponse;
import com.ddudu.application.common.port.reminder.in.CreateReminderUseCase;
import com.ddudu.application.common.port.reminder.in.UpdateReminderUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController implements ReminderControllerDoc {

  private final CreateReminderUseCase createReminderUseCase;
  private final UpdateReminderUseCase updateReminderUseCase;

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
  @PutMapping("/{id}")
  public ResponseEntity<Void> update(
      @Login
      Long loginId,
      @PathVariable("id")
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
