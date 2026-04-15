package com.modoo.application.common.dto.notification.response;

import lombok.Builder;

@Builder
public record NotificationInboxStatusResponse(boolean hasNew, long unreadCount) {

}
