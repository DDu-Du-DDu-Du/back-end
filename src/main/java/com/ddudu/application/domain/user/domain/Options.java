package com.ddudu.application.domain.user.domain;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Options {

  private final boolean allowingFollowsAfterApproval;
  private final boolean templateNotification;
  private final boolean dduduNotification;

  @Builder
  private Options(
      Boolean allowingFollowsAfterApproval, Boolean templateNotification, Boolean dduduNotification
  ) {
    this.allowingFollowsAfterApproval =
        Objects.nonNull(allowingFollowsAfterApproval) ? allowingFollowsAfterApproval : false;
    this.templateNotification = Objects.nonNull(templateNotification) ? templateNotification : true;
    this.dduduNotification = Objects.nonNull(dduduNotification) ? dduduNotification : true;
  }

  public Options() {
    this(null, null, null);
  }

  public Options switchOptions() {
    return Options.builder()
        .allowingFollowsAfterApproval(!this.allowingFollowsAfterApproval)
        .build();
  }

}
