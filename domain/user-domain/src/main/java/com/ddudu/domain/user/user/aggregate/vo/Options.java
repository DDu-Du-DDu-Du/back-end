package com.ddudu.domain.user.user.aggregate.vo;

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
      Boolean allowingFollowsAfterApproval,
      Boolean templateNotification,
      Boolean dduduNotification
  ) {
    this.allowingFollowsAfterApproval = Objects.requireNonNullElse(
        allowingFollowsAfterApproval,
        false
    );
    this.templateNotification = Objects.requireNonNullElse(templateNotification, true);
    this.dduduNotification = Objects.requireNonNullElse(dduduNotification, true);
  }

}
