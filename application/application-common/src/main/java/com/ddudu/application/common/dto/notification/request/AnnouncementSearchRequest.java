package com.ddudu.application.common.dto.notification.request;

import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import lombok.Getter;

@Getter
public final class AnnouncementSearchRequest {

  private final ScrollRequest scroll;

  public AnnouncementSearchRequest(String order, String cursor, Integer size) {
    this.scroll = new ScrollRequest(order, cursor, size);
  }

  public int getSize() {
    return scroll.getSize();
  }

}
