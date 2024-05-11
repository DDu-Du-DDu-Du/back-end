package com.ddudu.old.user.domain;

public enum FollowingStatus {
  FOLLOWING,
  REQUESTED,
  IGNORED;

  public boolean isModifiable() {
    return this != REQUESTED;
  }

}
