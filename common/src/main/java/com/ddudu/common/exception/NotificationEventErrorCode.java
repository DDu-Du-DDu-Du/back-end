package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationEventErrorCode implements ErrorCode {
  NULL_TYPE_CODE(10001, "알림 유형은 필수값입니다."),
  NULL_RECEIVER_ID(10002, "알림 수신자는 필수값입니다."),
  NULL_CONTEXT_ID(10003, "알림 대상 컨텍스트는 필수값입니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}
