package com.ddudu.old.like.exception;

import com.ddudu.application.exception.ErrorCode;

public enum LikeErrorCode implements ErrorCode {
  NULL_USER(7001, "사용자는 반드시 존재해야 합니다."),
  NULL_TODO(7002, "좋아요할 할 일은 반드시 존재해야 합니다."),
  SELF_LIKE_UNAVAILABLE(7003, "본인의 할 일에는 좋아요를 할 수 없습니다."),
  USER_NOT_EXISTING(7004, "좋아요를 보내려는 사용자를 찾을 수 없습니다."),
  TODO_NOT_EXISTING(7005, "좋아요를 보내려는 할 일을 찾을 수 없습니다."),
  LIKE_NOT_EXISTING(7006, "좋아요를 찾을 수 없습니다."),
  UNAVAILABLE_UNCOMPLETED_TODO(7007, "완료한 다음에 좋아요를 보낼 수 있습니다"),
  INVALID_AUTHORITY(7008, "해당 기능에 대한 사용자 권한이 없습니다.");

  private final int code;
  private final String message;

  LikeErrorCode(int code, String message) {
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
