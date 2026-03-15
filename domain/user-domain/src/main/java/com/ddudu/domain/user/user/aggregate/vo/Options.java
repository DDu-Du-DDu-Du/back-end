package com.ddudu.domain.user.user.aggregate.vo;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Options {

  private final boolean allowingFollowsAfterApproval;
  private final boolean templateNotification;
  private final boolean dduduNotification;
  private final DisplayOptions display;
  private final MenuActivationOptions menuActivation;
  private final AppConnectionOptions appConnection;

  @Builder
  private Options(
      Boolean allowingFollowsAfterApproval,
      Boolean templateNotification,
      Boolean dduduNotification,
      DisplayOptions display,
      MenuActivationOptions menuActivation,
      AppConnectionOptions appConnection
  ) {
    this.allowingFollowsAfterApproval =
        Objects.nonNull(allowingFollowsAfterApproval) && allowingFollowsAfterApproval;
    this.templateNotification = Objects.isNull(templateNotification) || templateNotification;
    this.dduduNotification = Objects.isNull(dduduNotification) || dduduNotification;
    this.display = Objects.requireNonNullElseGet(display, this::createDisplay);
    this.menuActivation = Objects.requireNonNullElseGet(
        menuActivation,
        this::createMenuActivation
    );
    this.appConnection = Objects.requireNonNullElseGet(
        appConnection,
        this::createAppConnection
    );
  }

  private DisplayOptions createDisplay() {
    return DisplayOptions.builder()
        .build();
  }

  private MenuActivationOptions createMenuActivation() {
    return MenuActivationOptions.builder()
        .build();
  }

  private AppConnectionOptions createAppConnection() {
    return AppConnectionOptions.builder()
        .build();
  }

}
