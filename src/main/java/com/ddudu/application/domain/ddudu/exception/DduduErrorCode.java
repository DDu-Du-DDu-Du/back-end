package com.ddudu.application.domain.ddudu.exception;

import com.ddudu.application.exception.ErrorCode;
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
  SHOULD_POSTPONE_UNTIL_FUTURE(2012, "미루기 날짜는 오늘 이후의 날짜여야 합니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }
}
