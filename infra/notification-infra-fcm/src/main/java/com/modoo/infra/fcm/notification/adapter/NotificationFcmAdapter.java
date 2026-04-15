package com.modoo.infra.fcm.notification.adapter;

import com.modoo.application.common.port.notification.out.NotificationSendPort;
import com.modoo.common.annotation.DrivenAdapter;
import com.modoo.common.util.FcmLogAction;
import com.modoo.infra.fcm.notification.config.FcmProperties;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.util.List;
import java.util.UUID;
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

    String sendId = UUID.randomUUID()
        .toString();
    long start = System.currentTimeMillis();

    BatchResponse batchResponse = sendMulticast(
        sendId,
        start,
        multicastMessage,
        deviceTokens.size()
    );

    handlePartialException(sendId, start, batchResponse);
  }

  private BatchResponse sendMulticast(
      String sendId,
      long start,
      MulticastMessage message,
      int tokenCount
  ) {
    log.info("{} sendId={} tokenCount={}", FcmLogAction.SEND.prefix(), sendId, tokenCount);

    try {
      return firebaseMessaging.sendEachForMulticast(
          message,
          fcmProperties.validateOnly()
      );
    } catch (FirebaseMessagingException e) {
      long durationMs = System.currentTimeMillis() - start;
      String exceptionSimpleName = e.getClass()
          .getSimpleName();

      log.error(
          "{} sendId={} durationMs={} exception={} code={} message={}",
          FcmLogAction.ERR.prefix(),
          sendId,
          durationMs,
          exceptionSimpleName,
          e.getMessagingErrorCode(),
          e.getMessage()
      );

      throw new IllegalStateException();
    }
  }

  private void handlePartialException(String sendId, long start, BatchResponse batchResponse) {
    long durationMs = System.currentTimeMillis() - start;
    int failureCount = batchResponse.getFailureCount();

    log.info(
        "{} sendId={} successCount={} failureCount={} durationMs={}",
        FcmLogAction.DONE.prefix(),
        sendId,
        batchResponse.getSuccessCount(),
        failureCount,
        durationMs
    );

    if (failureCount == 0) {
      return;
    }

    batchResponse.getResponses()
        .stream()
        .map(SendResponse::getException)
        .forEach(exception -> log.warn(
            "{} sendId={} code={} message={}",
            FcmLogAction.FAIL.prefix(),
            sendId,
            exception.getMessagingErrorCode(),
            exception.getMessage()
        ));

    // TODO: Retry 로직 등 추후 고려 필요
  }

}
