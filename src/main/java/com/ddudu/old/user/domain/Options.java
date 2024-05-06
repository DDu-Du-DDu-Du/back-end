package com.ddudu.old.user.domain;

import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class Options {

  private static final boolean DEFAULT_OFF = false;

  @Column(name = "follows_after_approval", nullable = false)
  private boolean allowingFollowsAfterApproval;

  public Options() {
    allowingFollowsAfterApproval = DEFAULT_OFF;
  }

  public Options(boolean isAllowed) {
    allowingFollowsAfterApproval = isAllowed;
  }

  public void switchOptions() {
    allowingFollowsAfterApproval = !allowingFollowsAfterApproval;
  }

}
