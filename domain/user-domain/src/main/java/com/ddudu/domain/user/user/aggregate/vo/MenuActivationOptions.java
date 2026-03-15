package com.ddudu.domain.user.user.aggregate.vo;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuActivationOptions {

  private final MenuActivationItem calendar;
  private final MenuActivationItem dashboard;
  private final MenuActivationItem stats;

  @Builder
  private MenuActivationOptions(
      MenuActivationItem calendar,
      MenuActivationItem dashboard,
      MenuActivationItem stats
  ) {
    this.calendar = Objects.isNull(calendar) ? MenuActivationItem.builder()
        .active(true)
        .priority(1)
        .build() : calendar;
    this.dashboard = Objects.isNull(dashboard) ? MenuActivationItem.builder()
        .active(true)
        .priority(2)
        .build() : dashboard;
    this.stats = Objects.isNull(stats) ? MenuActivationItem.builder()
        .active(true)
        .priority(3)
        .build() : stats;
  }

}
