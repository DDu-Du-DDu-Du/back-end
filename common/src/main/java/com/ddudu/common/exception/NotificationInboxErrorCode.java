package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationInboxErrorCode implements ErrorCode {
  NULL_TYPE_CODE(11001, "알림 유형은 필수값입니다."),
  NULL_USER_ID(11002, "알림 수신자는 필수값입니다."),
  NULL_CONTEXT_ID(11003, "알림 대상 컨텍스트는 필수값입니다."),
  NULL_EVENT_ID(11004, "일림 인박스의 알림 이벤트는 필수값입니다."),
  NULL_TITLE(11005, "알림의 타이틀은 필수값입니다."),
  EXCESSIVE_TITLE_LENGTH(11006, "알림 제목은 최대 50자입니다."),
  EXCESSIVE_BODY_LENGTH(11006, "알림 내용은 최대 200자입니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}
