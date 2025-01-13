package com.ddudu.application.domain.repeat_ddudu.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RepeatDduduErrorCode implements ErrorCode {
  BLANK_NAME(6001, "반복 뚜두명은 필수값입니다."),
  NULL_GOAL_VALUE(6002, "목표는 필수값입니다."),
  NULL_REPEAT_TYPE(6003, "반복 유형은 필수값입니다."),
  NULL_START_DATE(6004, "반복 시작 날짜는 필수값입니다."),
  NULL_END_DATE(6005, "반복 종료 날짜는 필수값입니다."),
  EXCESSIVE_NAME_LENGTH(6006, "반복 뚜두명이 최대 글자수를 초과했습니다."),
  UNABLE_TO_END_BEFORE_START(6007, "종료 날짜는 시작 날짜보다 뒤여야 합니다."),
  UNABLE_TO_FINISH_BEFORE_BEGIN(6008, "종료 시간은 시작 시간보다 뒤여야 합니다."),
  INVALID_REPEAT_TYPE(6009, "유효하지 않은 반복 유형입니다."),
  INVALID_DAY_OF_WEEK(6010, "유효하지 않은 요일입니다."),
  NULL_OR_EMPTY_REPEAT_DATES_OF_MONTH(6011, "반복되는 날짜가 없습니다."),
  NULL_OR_EMPTY_REPEAT_DAYS_OF_WEEK(6012, "반복되는 요일이 없습니다."),
  NULL_LAST_DAY(6013, "마지막 날 반복 여부는 필수값입니다."),
  INVALID_GOAL(6014, "유효하지 않은 목표입니다."),
  REPEAT_DDUDU_NOT_EXIST(6015, "해당 아이디를 가진 반복 뚜두가 존재하지 않습니다."),
  GOAL_ALREADY_DONE(6016, "목표가 이미 완료되었습니다.");;

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
