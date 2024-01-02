package com.ddudu.user.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Options {

  private boolean allowingFollowsAfterApproval;

  protected Options() {
    allowingFollowsAfterApproval = false;
  }

  public void toggleFollowsAfterApproval() {
    allowingFollowsAfterApproval = !allowingFollowsAfterApproval;
  }

}
