package com.ddudu.domain.notification.event.aggregate.enums;

import java.text.MessageFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationEventTypeCode {
  DDUDU_REMINDER("뚜두 시작 {0} 전에 알려드려요."),
  TEMPLATE_COMMENT(""),
  TEMPLATE_LIKE(""),
  FOLLOWING_REQUEST(""),
  FOLLOWING_RESPONSE(""),
  FOLLOWING_RECEIVED(""),
  ANNOUNCE("");

  private final String template;

  public String formatBody(Object... arguments) {
    return MessageFormat.format(template, arguments);
  }

}
