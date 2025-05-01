package com.ddudu.application.domain.user.domain.enums;

public enum FollowingStatus {
  FOLLOWING,
  REQUESTED,
  IGNORED;

  public boolean isModifiable() {
    return this != REQUESTED;
  }

}
