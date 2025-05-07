package com.ddudu.common.exception;

public enum FollowingErrorCode implements ErrorCode {
  NULL_FOLLOWER(6001, "팔로워가 반드시 존재해야 합니다."),
  NULL_FOLLOWEE(6002, "팔로우 대상이 반드시 존재해야 합니다."),
  SELF_FOLLOWING_UNAVAILABLE(6003, "본인을 팔로우 할 수 없습니다."),
  FOLLOWER_NOT_EXISTING(6004, "팔로잉을 요청한 사용자를 찾을 수 없습니다."),
  FOLLOWEE_NOT_EXISTING(6005, "팔로잉 대상을 찾을 수 없습니다."),
  ALREADY_FOLLOWING(6006, "이미 팔로우하고 있는 사용자입니다."),
  NULL_STATUS_REQUESTED(6007, "요청할 팔로잉 상태는 필수값입니다."),
  ID_NOT_EXISTING(6008, "해당 아이디의 팔로잉이 존재하지 않습니다."),
  REQUEST_UNAVAILABLE(6009, "이미 생성된 팔로잉을 요청 상태로 변경할 수 없습니다."),
  WRONG_OWNER(6010, "해당 사용자가 생성한 팔로잉이 아닙니다."),
  NOT_ENGAGED_USER(6011, "해당 사용자와 관련되지 않은 팔로잉 요청입니다.");

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
