package com.ddudu.application.domain.repeatitive_ddudu.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RepeatitiveDduduErrorCode implements ErrorCode {
  BLANK_NAME(6001, "반복 뚜두명은 필수값입니다."),
  NULL_GOAL_VALUE(6002, "목표는 필수값입니다."),
  NULL_REPEAT_TYPE(6003, "반복 유형은 필수값입니다."),
  NULL_START_DATE(6004, "반복 시작 날짜는 필수값입니다."),
  NULL_END_DATE(6005, "반복 종료 날짜는 필수값입니다."),
  EXCESSIVE_NAME_LENGTH(6006, "반복 뚜두명이 최대 글자수를 초과했습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
