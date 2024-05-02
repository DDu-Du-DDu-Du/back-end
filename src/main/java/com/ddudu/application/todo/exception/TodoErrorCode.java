package com.ddudu.application.todo.exception;

import com.ddudu.application.common.exception.ErrorCode;

public enum TodoErrorCode implements ErrorCode {

  NULL_GOAL_VALUE(2001, "목표는 필수값입니다."),
  BLANK_NAME(2002, "할 일은 필수값입니다."),
  EXCESSIVE_NAME_LENGTH(2003, "할 일 내용이 최대 글자수를 초과했습니다."),
  ID_NOT_EXISTING(2004, "할 일 아이디가 존재하지 않습니다."),
  GOAL_NOT_EXISTING(2005, "목표 아이디가 존재하지 않습니다."),
  USER_NOT_EXISTING(2006, "사용자 아이디가 존재하지 않습니다."),
  INVALID_AUTHORITY(2007, "해당 기능에 대한 사용자 권한이 없습니다."),
  LOGIN_USER_NOT_EXISTING(2008, "로그인 아이디가 존재하지 않습니다.");

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
