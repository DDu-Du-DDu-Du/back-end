package com.ddudu.api.notification.inbox.controller;

import com.ddudu.api.notification.inbox.doc.NotificationInboxControllerDoc;
import com.ddudu.application.common.dto.notification.request.NotificationInboxSearchRequest;
import com.ddudu.application.common.dto.notification.response.NotificationInboxSearchResponse;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.notification.in.NotificationInboxSearchUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification-inboxes")
@RequiredArgsConstructor
public class NotificationInboxController implements NotificationInboxControllerDoc {

  private final NotificationInboxSearchUseCase notificationInboxSearchUseCase;

  @Override
  @GetMapping
  public ResponseEntity<ScrollResponse<NotificationInboxSearchResponse>> getList(
      @Login
      Long loginId,
      NotificationInboxSearchRequest request
  ) {
    ScrollResponse<NotificationInboxSearchResponse> response = notificationInboxSearchUseCase.search(
        loginId,
        request
    );

    return ResponseEntity.ok(response);
  }

}
