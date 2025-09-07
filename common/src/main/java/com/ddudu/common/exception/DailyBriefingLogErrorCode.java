package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DailyBriefingLogErrorCode implements ErrorCode {
  NULL_USER_ID(11001, "데일리 사전 요약 발송 대상 유저는 필수값입니다."),
  LOGIN_USER_NOT_EXISTING(11002, "로그인 사용자를 찾을 수 없습니다");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}
