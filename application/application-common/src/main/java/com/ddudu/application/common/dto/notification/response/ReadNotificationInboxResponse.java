package com.ddudu.application.common.dto.notification.response;

import lombok.Builder;

@Builder
public record ReadNotificationInboxResponse(String context, Long contextId) {

}
