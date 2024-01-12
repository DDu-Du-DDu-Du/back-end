package com.ddudu.following.domain;

public enum FollowingStatus {
  FOLLOWING,
  REQUESTED,
  IGNORED;

  public boolean isModifiable() {
    return this != REQUESTED;
  }

}
