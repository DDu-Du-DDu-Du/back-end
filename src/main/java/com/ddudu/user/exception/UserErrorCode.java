package com.ddudu.user.exception;

import com.ddudu.common.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {

  BLANK_EMAIL(1001, "이메일이 입력되지 않았습니다."),
  BLANK_PASSWORD(1002, "비밀번호가 입력되지 않았습니다."),
  BLANK_OPTIONAL_USERNAME(1003, "아이디는 공백일 수 없습니다."),
  BLANK_NICKNAME(1004, "닉네임이 입력되지 않았습니다."),
  INSUFFICIENT_PASSWORD_LENGTH(1005, "비밀번호는 8자리 이상이어야 합니다."),
  EXCESSIVE_NICKNAME_LENGTH(1006, "닉네임은 최대 20자 입니다."),
  EXCESSIVE_OPTIONAL_USERNAME_LENGTH(1007, "아이디는 최대 20자 입니다."),
  EXCESSIVE_INTRODUCTION_LENGTH(1008, "자기소개는 최대 50자 입니다."),
  INVALID_EMAIL_FORMAT(1009, "유효하지 않은 이메일 형식입니다."),
  INVALID_PASSWORD_FORMAT(1010, "비밀번호는 영문, 숫자, 특수문자로 구성되어야 합니다."),
  DUPLICATE_EMAIL(1011, "이미 존재하는 이메일입니다."),
  DUPLICATE_OPTIONAL_USERNAME(1012, "이미 존재하는 아이디입니다.");

  private final int code;
  private final String message;

  UserErrorCode(int code, String message) {
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
