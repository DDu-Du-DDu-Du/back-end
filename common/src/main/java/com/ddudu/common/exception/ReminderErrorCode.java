package com.ddudu.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ReminderErrorCode implements ErrorCode {
  NULL_USER(2101, "사용자는 필수값입니다."),
  NULL_TODO_VALUE(2102, "투두는 필수값입니다."),
  NULL_REMINDS_AT(2103, "미리알림 시간은 필수값입니다."),
  NULL_SCHEDULED_AT(2104, "일정 시간은 필수값입니다."),
  INVALID_REMINDS_AT(2105, "미리알림 시간은 일정 시간보다 이전이어야 합니다."),
  LOGIN_USER_NOT_EXISTING(2106, "로그인 아이디가 존재하지 않습니다."),
  TODO_NOT_EXISTING(2107, "할 일 아이디가 존재하지 않습니다."),
  INVALID_AUTHORITY(2108, "해당 기능에 대한 사용자 권한이 없습니다."),
  UNABLE_TO_GET_REMINDER(2109, "미리알림 시간을 알 수 없는 상태입니다."),
  REMINDER_NOT_AFTER_NOW(2110, "미리알림 시간은 현재 시간보다 이후여야 합니다."),
  ALREADY_REMINDED(2111, "이미 발송된 미리알림은 취소할 수 없습니다."),
  REMINDER_NOT_EXISTING(2112, "미리알림 아이디가 존재하지 않습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
