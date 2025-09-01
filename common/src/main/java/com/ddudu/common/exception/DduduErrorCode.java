package com.ddudu.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum DduduErrorCode implements ErrorCode {
  NULL_GOAL_VALUE(2001, "목표는 필수값입니다."),
  BLANK_NAME(2002, "할 일은 필수값입니다."),
  EXCESSIVE_NAME_LENGTH(2003, "할 일 내용이 최대 글자수를 초과했습니다."),
  ID_NOT_EXISTING(2004, "할 일 아이디가 존재하지 않습니다."),
  GOAL_NOT_EXISTING(2005, "목표 아이디가 존재하지 않습니다."),
  USER_NOT_EXISTING(2006, "사용자 아이디가 존재하지 않습니다."),
  INVALID_AUTHORITY(2007, "해당 기능에 대한 사용자 권한이 없습니다."),
  LOGIN_USER_NOT_EXISTING(2008, "로그인 아이디가 존재하지 않습니다."),
  NULL_USER(2009, "사용자는 필수값입니다."),
  UNABLE_TO_FINISH_BEFORE_BEGIN(2010, "종료 시간은 시작 시간보다 뒤여야 합니다."),
  NULL_DATE_TO_MOVE(2011, "변경할 날짜가 누락됐습니다."),
  SHOULD_POSTPONE_UNTIL_FUTURE(2012, "미루기 날짜는 오늘 이후의 날짜여야 합니다."),
  UNABLE_TO_REPRODUCE_ON_SAME_DATE(2013, "다시 하기의 날짜는 기존과 달라야합니다."),
  NEGATIVE_OR_ZERO_GOAL_ID(2014, "목표 ID는 양수입니다."),
  NULL_SCHEDULED_DATE(2015, "날짜는 필수값입니다."),
  GOAL_ALREADY_DONE(2016, "목표가 이미 완료되었습니다."),
  BEGIN_AT_REQUIRED_FOR_REMINDER(2017, "시작 시간 없이 미리알림을 설정할 수 없습니다."),
  REMINDER_NOT_AFTER_NOW(2018, "미리알림 시간은 현재 시간보다 이후여야 합니다."),
  ZERO_REMINDER(2019, "미리알림 입력값이 없습니다."),
  NEGATIVE_REMINDER_INPUT_EXISTS(2020, "미리알림을 입력값 중 음수가 포함되어 있습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
