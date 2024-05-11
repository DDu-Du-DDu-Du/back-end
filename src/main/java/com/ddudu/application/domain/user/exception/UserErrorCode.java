package com.ddudu.application.domain.user.exception;

import com.ddudu.application.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {
  BLANK_USERNAME(1001, "아이디는 공백일 수 없습니다."),
  BLANK_NICKNAME(1002, "닉네임이 입력되지 않았습니다."),
  EXCESSIVE_NICKNAME_LENGTH(1003, "닉네임이 최대 글자 수를 초과했습니다."),
  EXCESSIVE_USERNAME_LENGTH(1004, "아이디가 최대 글자 수를 초과했습니다."),
  EXCESSIVE_INTRODUCTION_LENGTH(1005, "자기소개가 최대 글자 수를 초과했습니다."),
  DUPLICATE_USERNAME(1006, "이미 존재하는 아이디입니다."),
  INVALID_AUTHENTICATION(1007, "토큰과 사용자의 정보가 일치하지 않습니다."),
  ID_NOT_EXISTING(1008, "해당 아이디를 가진 사용자가 존재하지 않습니다."),
  INVALID_AUTHORITY(1009, "해당 기능에 대한 사용자 권한이 없습니다."),
  INVALID_PROVIDER_TYPE(1010, "없는 소셜 로그인 공급자입니다."),
  BLANK_PROVIDER_ID(1011, "존재하지 않는 소셜 유저 아이디입니다."),
  EXCESSIVE_PROFILE_IMAGE_URL_LENGTH(1012, "프로필 사진 URL이 최대 글자 수를 초과했습니다.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}
