package com.ddudu.api.planning.reminder.controller;

import com.ddudu.api.planning.reminder.doc.ReminderControllerDoc;
import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.reminder.request.CreateReminderRequest;
import com.ddudu.application.common.dto.reminder.response.CreateReminderResponse;
import com.ddudu.application.common.dto.reminder.response.RetrieveReminderResponse;
import com.ddudu.application.common.port.reminder.in.CreateReminderUseCase;
import com.ddudu.application.common.port.reminder.in.RetrieveRemindersUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

}
