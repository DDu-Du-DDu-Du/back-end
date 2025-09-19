package com.ddudu.infra.fcm.notification.adapter;

import com.ddudu.application.common.port.notification.out.NotificationSendPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.infra.fcm.notification.config.FcmProperties;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DrivenAdapter
@RequiredArgsConstructor
@Slf4j
public class NotificationFcmAdapter implements NotificationSendPort {

  private final FirebaseMessaging firebaseMessaging;
  private final FcmProperties fcmProperties;

  @Override
  public void sendToDevices(
      List<String> deviceTokens,
      String title,
      String body
  ) {
    Notification notification = Notification.builder()
        .setTitle(title)
        .setBody(body)
        .build();
    MulticastMessage multicastMessage = MulticastMessage.builder()
        .setNotification(notification)
        .addAllTokens(deviceTokens)
        .build();
    BatchResponse batchResponse = sendMulticast(multicastMessage);

    handlePartialException(batchResponse);
  }

  private BatchResponse sendMulticast(MulticastMessage message) {
    try {
      return firebaseMessaging.sendEachForMulticast(
          message,
          fcmProperties.validateOnly()
      );
    } catch (FirebaseMessagingException e) {
      log.warn(
          "All of firebase multicast failed: [{}] {}",
          e.getMessagingErrorCode(),
          e.getMessage()
      );

      throw new IllegalStateException();
    }
  }

  private void handlePartialException(BatchResponse batchResponse) {
    int failureCount = batchResponse.getFailureCount();

    if (failureCount == 0) {
      return;
    }

    log.warn("{} of firebase multicast failed", failureCount);

    batchResponse.getResponses()
        .stream()
        .map(SendResponse::getException)
        .forEach(exception -> log.warn(
            "[{}] {}",
            exception.getMessagingErrorCode(),
            exception.getMessage()
        ));

    // TODO: Retry 로직 등 추후 고려 필요
  }

}
