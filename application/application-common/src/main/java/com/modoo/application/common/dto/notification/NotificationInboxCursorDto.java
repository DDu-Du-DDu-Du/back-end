package com.modoo.application.common.dto.notification;

public record NotificationInboxCursorDto(
    String cursor,
    NotificationInboxSearchDto notificationInbox
) {

}
