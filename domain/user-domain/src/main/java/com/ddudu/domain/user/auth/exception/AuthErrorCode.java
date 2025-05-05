package com.ddudu.domain.user.auth.exception;

import com.ddudu.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {
  INVALID_TOKEN_AUTHORITY(5001, "유효하지 않은 토큰 권한입니다."),
  BAD_TOKEN_CONTENT(5002, "유효하지 않은 토큰 형식입니다."),
  INVALID_AUTHORITY(5003, "해당 기능에 대한 사용자 권한이 없습니다."),
  UNABLE_TO_PARSE_USER_FAMILY_VALUE(5004, "파싱할 수 없는 값입니다."),
  INVALID_USER_ID_FOR_REFRESH_TOKEN(5005, "리프레시 토큰의 사용자 아이디가 올바르지 않습니다."),
  INVALID_REFRESH_TOKEN_FAMILY(5006, "리프레시 토큰 패밀리가 올바르지 않습니다."),
  REFRESH_NOT_ALLOWED(5007, "잘못된 리프레시 토큰입니다. 갱신할 수 없습니다."),
  USER_NOT_FOUND(5008, "토큰을 생성할 사용자를 찾을 수 없습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
