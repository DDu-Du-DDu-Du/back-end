package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationDeviceTokenErrorCode implements ErrorCode {
  NULL_USER_ID(12001, "디바이스 토큰의 수신자는 필수값입니다."),
  INVALID_CHANNEL(12002, "유효하지 않은 채널 유형입니다."),
  NULL_TOKEN(12003, "디바이스 토큰 값은 필수값입니다."),
  EXCESSIVE_TOKEN_LENGTH(12004, "디바이스 토큰의 최대 길이는 512입니다."),
  LOGIN_USER_NOT_EXISTING(12005, "로그인 사용자가 존재하지 않습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}

