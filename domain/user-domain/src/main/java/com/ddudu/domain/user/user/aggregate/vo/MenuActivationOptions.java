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
    this.calendar = Objects.requireNonNullElseGet(calendar, this::createDefaultCalendar);
    this.dashboard = Objects.requireNonNullElseGet(dashboard, this::createDefaultDashboard);
    this.stats = Objects.requireNonNullElseGet(stats, this::createDefaultStats);
  }

  private MenuActivationItem createDefaultCalendar() {
    return MenuActivationItem.builder()
        .active(true)
        .priority(1)
        .build();
  }

  private MenuActivationItem createDefaultDashboard() {
    return MenuActivationItem.builder()
        .active(true)
        .priority(2)
        .build();
  }

  private MenuActivationItem createDefaultStats() {
    return MenuActivationItem.builder()
        .active(true)
        .priority(3)
        .build();
  }

}
