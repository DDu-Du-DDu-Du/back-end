package com.ddudu.application.domain.user.domain;

import lombok.Getter;

@Getter
public class Options {

  private static final boolean DEFAULT_OFF = false;

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
