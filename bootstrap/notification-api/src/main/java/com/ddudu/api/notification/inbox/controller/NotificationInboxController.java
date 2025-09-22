package com.ddudu.api.notification.inbox.controller;

import com.ddudu.api.notification.inbox.doc.NotificationInboxControllerDoc;
import com.ddudu.application.common.dto.notification.request.NotificationInboxSearchRequest;
import com.ddudu.application.common.dto.notification.response.NotificationInboxSearchResponse;
import com.ddudu.application.common.dto.notification.response.ReadNotificationInboxResponse;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.notification.in.NotificationInboxSearchUseCase;
import com.ddudu.application.common.port.notification.in.ReadNotificationInboxUseCase;
import com.ddudu.bootstrap.common.annotation.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification-inboxes")
@RequiredArgsConstructor
public class NotificationInboxController implements NotificationInboxControllerDoc {

  private final NotificationInboxSearchUseCase notificationInboxSearchUseCase;
  private final ReadNotificationInboxUseCase readNotificationInboxUseCase;

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

  @Override
  @PatchMapping("/{id}/read")
  public ResponseEntity<ReadNotificationInboxResponse> read(
      @Login
      Long loginId,
      @PathVariable("id")
      Long notificationInboxId
  ) {
    ReadNotificationInboxResponse response = readNotificationInboxUseCase.read(
        loginId,
        notificationInboxId
    );

    return ResponseEntity.ok(response);
  }

}
