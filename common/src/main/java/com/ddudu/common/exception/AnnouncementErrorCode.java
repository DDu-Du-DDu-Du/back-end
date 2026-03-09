package com.ddudu.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AnnouncementErrorCode implements ErrorCode {
  NULL_TITLE(13001, "Announcement title is required."),
  EXCESSIVE_TITLE_LENGTH(13002, "Announcement title must be 50 characters or fewer."),
  NULL_CONTENTS(13003, "Announcement contents are required."),
  EXCESSIVE_CONTENTS_LENGTH(13004, "Announcement contents must be 2000 characters or fewer."),
  NULL_USER_ID(13005, "Announcement author id is required."),
  LOGIN_USER_NOT_EXISTING(13006, "Login user does not exist."),
  INVALID_AUTHORITY(13007, "User is not authorized to perform this action."),
  ANNOUNCEMENT_NOT_EXISTING(13008, "Announcement does not exist.");

  private final int code;
  private final String message;

  @Override
  public String getCodeName() {
    return this.code + " " + this.name();
  }

}
