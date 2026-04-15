package com.modoo.domain.user.user.aggregate.enums;

public enum FollowingStatus {
  FOLLOWING,
  REQUESTED,
  IGNORED;

  public boolean isModifiable() {
    return this != REQUESTED;
  }

}
