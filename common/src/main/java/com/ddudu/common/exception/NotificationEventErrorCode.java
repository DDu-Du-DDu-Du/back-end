package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationEventErrorCode implements ErrorCode {
  NULL_TYPE_CODE(10001, "알림 유형은 필수값입니다."),
  NULL_RECEIVER_ID(10002, "알림 수신자는 필수값입니다."),
  NULL_CONTEXT_ID(10003, "알림 대상 컨텍스트는 필수값입니다."),
  CANNOT_MODIFY_FIRED_EVENT(10004, "이미 발송된 알림은 수정할 수 없습니다."),
  CANNOT_FIRE_AT_PAST(10005, "발송 예정 시간은 현재시간보다 뒤여야 합니다."),
  NOTIFICATION_EVENT_NOT_EXISTING(10006, "해당 아이디의 알림 이벤트가 존재하지 않습니다."),
  ORIGINAL_DDUDU_NOT_EXISTING(10007, "해당 알림 이벤트의 기존 뚜두가 없습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}
