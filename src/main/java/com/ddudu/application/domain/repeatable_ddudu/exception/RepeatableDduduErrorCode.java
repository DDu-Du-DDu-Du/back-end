package com.ddudu.application.domain.repeatable_ddudu.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RepeatableDduduErrorCode implements ErrorCode {
  BLANK_NAME(6001, "반복 뚜두명은 필수값입니다."),
  NULL_GOAL_VALUE(6002, "목표는 필수값입니다."),
  NULL_REPEAT_TYPE(6003, "반복 유형은 필수값입니다."),
  NULL_START_DATE(6004, "반복 시작 날짜는 필수값입니다."),
  NULL_END_DATE(6005, "반복 종료 날짜는 필수값입니다."),
  EXCESSIVE_NAME_LENGTH(6006, "반복 뚜두명이 최대 글자수를 초과했습니다."),
  UNABLE_TO_END_BEFORE_START(6007, "종료 날짜는 시작 날짜보다 뒤여야 합니다."),
  UNABLE_TO_FINISH_BEFORE_BEGIN(6008, "종료 시간은 시작 시간보다 뒤여야 합니다."),
  INVALID_REPEAT_TYPE(6009, "유효하지 않은 반복 유형입니다."),
  INVALID_DAY_OF_WEEK(6010, "유효하지 않은 요일입니다.");


  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
