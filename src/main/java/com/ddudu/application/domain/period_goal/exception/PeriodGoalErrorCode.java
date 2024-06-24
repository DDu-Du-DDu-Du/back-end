package com.ddudu.application.domain.period_goal.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PeriodGoalErrorCode implements ErrorCode {
  CONTENTS_NOT_EXISTING(4001, "기간 목표의 내용은 필수값입니다."),
  PERIOD_GOAL_TYPE_NOT_EXISTING(4002, "기간 목표의 타입은 필수값입니다"),
  PLAN_DATE_NOT_EXISTING(4003, "기간 목표의 날짜는 필수값입니다."),
  USER_NOT_EXISTING(4004, "기간 목표의 사용자는 필수값입니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
