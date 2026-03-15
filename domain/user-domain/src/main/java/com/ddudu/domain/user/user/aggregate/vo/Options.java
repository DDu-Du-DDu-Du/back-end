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
    this.display = createDisplay(display);
    this.menuActivation = createMenuActivation(menuActivation);
    this.appConnection = createAppConnection(appConnection);
  }

  private DisplayOptions createDisplay(DisplayOptions display) {
    if (Objects.nonNull(display)) {
      return display;
    }

    return DisplayOptions.builder()
        .build();
  }

  private MenuActivationOptions createMenuActivation(MenuActivationOptions menuActivation) {
    if (Objects.nonNull(menuActivation)) {
      return menuActivation;
    }

    return MenuActivationOptions.builder()
        .build();
  }

  private AppConnectionOptions createAppConnection(AppConnectionOptions appConnection) {
    if (Objects.nonNull(appConnection)) {
      return appConnection;
    }

    return AppConnectionOptions.builder()
        .build();
  }

}
