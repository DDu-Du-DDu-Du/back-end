package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PeriodGoalErrorCode implements ErrorCode {
  CONTENTS_NOT_EXISTING(4001, "기간 목표의 내용은 필수값입니다."),
  PERIOD_GOAL_TYPE_NOT_EXISTING(4002, "기간 목표의 타입은 필수값입니다"),
  PLAN_DATE_NOT_EXISTING(4003, "기간 목표의 날짜는 필수값입니다."),
  USER_NOT_EXISTING(4004, "기간 목표의 사용자는 필수값입니다."),
  PERIOD_GOAL_NOT_EXISTING(4005, "기간 목표가 존재하지 않습니다."),
  INVALID_AUTHORITY(4006, "해당 기간 목표에 대한 권한이 없습니다."),
  INVALID_PERIOD_GOAL_TYPE_STATUS(4007, "존재하지 않은 기간 목표 타입입니다."),
  ;

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
