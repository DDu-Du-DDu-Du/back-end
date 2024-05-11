package com.ddudu.application.domain.authentication.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {
  INVALID_TOKEN_AUTHORITY(5001, "유효하지 않은 토큰 권한입니다."),
  BAD_TOKEN_CONTENT(5002, "유효하지 않은 토큰 형식입니다."),
  INVALID_AUTHORITY(5003, "해당 기능에 대한 사용자 권한이 없습니다."),
  INVALID_AUTHENTICATION(5004, "인증 정보가 유효하지 않거나 누락되었습니다.");

  private final int code;
  private final String message;


  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
