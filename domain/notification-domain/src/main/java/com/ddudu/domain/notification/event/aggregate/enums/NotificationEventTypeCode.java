package com.ddudu.domain.notification.event.aggregate.enums;

import java.text.MessageFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationEventTypeCode {
  DDUDU_REMINDER("DDUDU", "뚜두 시작 {0} 전에 알려드려요."),
  TEMPLATE_COMMENT("TEMPLATE", ""),
  TEMPLATE_LIKE("TEMPLATE", ""),
  FOLLOWING_REQUEST("FOLLOWING", ""),
  FOLLOWING_RESPONSE("FOLLOWING", ""),
  FOLLOWING_RECEIVED("FOLLOWING", ""),
  ANNOUNCE("ANNOUNCE", "");

  private final String upstreamContext;
  private final String template;

  public String formatBody(Object... arguments) {
    return MessageFormat.format(template, arguments);
  }

}
