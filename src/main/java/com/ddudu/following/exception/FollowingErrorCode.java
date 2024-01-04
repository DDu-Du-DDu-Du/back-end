package com.ddudu.following.exception;

import com.ddudu.common.exception.ErrorCode;

public enum FollowingErrorCode implements ErrorCode {
  NULL_FOLLOWER(6001, "팔로워가 반드시 존재해야 합니다."),
  NULL_FOLLOWEE(6002, "팔로우 대상이 반드시 존재해야 합니다."),
  SELF_FOLLOWING_UNAVAILABLE(6003, "본인을 팔로우 할 수 없습니다.");

  private final int code;
  private final String message;

  FollowingErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
