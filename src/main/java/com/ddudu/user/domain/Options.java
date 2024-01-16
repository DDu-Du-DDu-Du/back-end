package com.ddudu.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Options {

  private static final boolean DEFAULT_OFF = false;

  @Column(name = "follows_after_approval", nullable = false)
  private boolean allowingFollowsAfterApproval;

  protected Options() {
    allowingFollowsAfterApproval = DEFAULT_OFF;
  }

  public void switchOptions() {
    allowingFollowsAfterApproval = !allowingFollowsAfterApproval;
  }

}
