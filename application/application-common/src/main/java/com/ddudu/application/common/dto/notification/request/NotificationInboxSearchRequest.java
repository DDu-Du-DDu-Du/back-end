package com.ddudu.application.common.dto.notification.request;

import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import lombok.Getter;

@Getter
public class NotificationInboxSearchRequest {

  private final ScrollRequest scroll;

  public NotificationInboxSearchRequest(String cursor, Integer size) {
    this.scroll = new ScrollRequest(null, cursor, size);
  }

}
