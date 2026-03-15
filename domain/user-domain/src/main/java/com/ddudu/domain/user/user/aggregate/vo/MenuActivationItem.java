package com.ddudu.domain.user.user.aggregate.vo;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuActivationItem {

  private final boolean active;
  private final int priority;

  @Builder
  private MenuActivationItem(Boolean active, Integer priority) {
    this.active = Objects.isNull(active) || active;
    this.priority = Objects.isNull(priority) ? 1 : priority;
  }

}
