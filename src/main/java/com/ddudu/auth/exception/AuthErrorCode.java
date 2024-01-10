package com.ddudu.auth.exception;

import com.ddudu.common.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
  INVALID_TOKEN_AUTHORITY(5001, "유효하지 않은 토큰 권한입니다."),
  BAD_TOKEN_CONTENT(5002, "유효하지 않은 토큰 형식입니다."),
  EMAIL_NOT_EXISTING(5002, "이메일을 찾을 수 없습니다."),
  BAD_CREDENTIALS(5003, "잘못된 비밀번호 입니다."),
  INVALID_AUTHENTICATION(5004, "인증 정보가 유효하지 않거나 누락되었습니다.");

  private final int code;
  private final String message;

  AuthErrorCode(int code, String message) {
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
