package com.ddudu.todo.exception;

import com.ddudu.common.exception.ErrorCode;

public enum TodoErrorCode implements ErrorCode {

  NULL_GOAL_VALUE(2001, "목표는 필수값입니다."),
  BLANK_NAME(2002, "할 일은 필수값입니다."),
  EXCESSIVE_NAME_LENGTH(2003, "할 일 내용이 최대 글자수를 초과했습니다."),
  ID_NOT_EXISTING(2004, "할 일 아이디가 존재하지 않습니다.");

  private final int code;
  private final String message;

  TodoErrorCode(int code, String message) {
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
