package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AnnouncementErrorCode implements ErrorCode {
  NULL_TITLE(13001, "공지사항 제목은 필수값입니다."),
  EXCESSIVE_TITLE_LENGTH(13002, "공지사항 제목은 최대 50자입니다."),
  NULL_CONTENTS(13003, "공지사항 내용은 필수값입니다."),
  EXCESSIVE_CONTENTS_LENGTH(13004, "공지사항 내용은 최대 2000자입니다."),
  NULL_USER_ID(13005, "공지사항 작성자 아이디는 필수값입니다."),
  LOGIN_USER_NOT_EXISTING(13006, "로그인 사용자가 존재하지 않습니다."),
  INVALID_AUTHORITY(13007, "해당 기능에 대한 사용자 권한이 없습니다."),
  ANNOUNCEMENT_NOT_EXISTING(13008, "해당 아이디의 공지사항이 존재하지 않습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}
