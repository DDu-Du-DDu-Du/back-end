package com.modoo.application.common.dto.notification.request;

import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import lombok.Getter;

@Getter
public class NotificationInboxSearchRequest {

  private final ScrollRequest scroll;

  public NotificationInboxSearchRequest(String cursor, Integer size) {
    this.scroll = new ScrollRequest(null, cursor, size);
  }

}
