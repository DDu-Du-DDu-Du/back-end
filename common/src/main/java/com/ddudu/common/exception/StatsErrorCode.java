package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatsErrorCode implements ErrorCode {
  INVALID_DDUDU_STATUS(9001, "유효한 뚜두 상태가 아닙니다."),
  LOGIN_USER_NOT_EXISTING(9002, "로그인 사용자가 존재하지 않습니다."),
  USER_NOT_EXISTING(9003, "사용자가 존재하지 않습니다."),
  MONTHLY_STATS_EMPTY(9004, "월 통계 데이터가 없습니다."),
  MONTHLY_STATS_NOT_GROUPED_BY_GOAL(9005, "다른 목표의 스탯이 포함되어 있습니다.")
  ;

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
