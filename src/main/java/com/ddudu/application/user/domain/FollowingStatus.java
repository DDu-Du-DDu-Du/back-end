package com.ddudu.application.user.domain;

public enum FollowingStatus {
  FOLLOWING,
  REQUESTED,
  IGNORED;

  public boolean isModifiable() {
    return this != REQUESTED;
  }

}
