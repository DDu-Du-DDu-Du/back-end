package com.ddudu.old.user.dto;

public enum FollowingSearchType {
  FOLLOWEE,
  FOLLOWER;

  public static boolean isSearchingFollower(FollowingSearchType searchType) {
    return searchType == FOLLOWER;
  }
}
