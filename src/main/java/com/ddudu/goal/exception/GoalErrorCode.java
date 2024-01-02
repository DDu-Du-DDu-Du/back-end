package com.ddudu.goal.exception;

import com.ddudu.common.exception.ErrorCode;

public enum GoalErrorCode implements ErrorCode {
  BLANK_NAME(3001, "목표명은 필수값입니다."),
  EXCESSIVE_NAME_LENGTH(3002, "목표명이 최대 글자수를 초과했습니다."),
  INVALID_COLOR_FORMAT(3003, "올바르지 않은 색상 코드입니다."),
  ID_NOT_EXISTING(3004, "해당 아이디를 가진 목표가 존재하지 않습니다."),
  NULL_STATUS(3005, "목표 상태가 입력되지 않았습니다."),
  BLANK_COLOR(3006, "색상이 입력되지 않았습니다."),
  NULL_PRIVACY_TYPE(3007, "공개 설정이 입력되지 않았습니다."),
  USER_NOT_EXISTING(3008, "해당 아이디를 가진 사용자가 존재하지 않습니다.");

  private final int code;
  private final String message;

  GoalErrorCode(int code, String message) {
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
