package com.ddudu.like.exception;

import com.ddudu.common.exception.ErrorCode;

public enum LikeErrorCode implements ErrorCode {
  NULL_USER(7001, "사용자가 반드시 존재해야 합니다."),
  NULL_TODO(7002, "좋아요할 할 일이 반드시 존재해야 합니다."),
  SELF_LIKE_UNAVAILABLE(7003, "본인의 할 일에 좋아요를 할 수 없습니다.");

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
