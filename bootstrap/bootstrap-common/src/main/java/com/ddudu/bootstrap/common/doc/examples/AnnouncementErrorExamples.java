package com.ddudu.bootstrap.common.doc.examples;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnouncementErrorExamples {

  public static final String NULL_TITLE = """
      {
        "code": 13001,
        "message": "공지사항 제목은 필수값입니다."
      }
      """;

  public static final String EXCESSIVE_TITLE_LENGTH = """
      {
        "code": 13002,
        "message": "공지사항 제목은 최대 50자입니다."
      }
      """;

  public static final String NULL_CONTENTS = """
      {
        "code": 13003,
        "message": "공지사항 내용은 필수값입니다."
      }
      """;

  public static final String EXCESSIVE_CONTENTS_LENGTH = """
      {
        "code": 13004,
        "message": "공지사항 내용은 최대 2000자입니다."
      }
      """;

  public static final String LOGIN_USER_NOT_EXISTING = """
      {
        "code": 13006,
        "message": "로그인 사용자가 존재하지 않습니다."
      }
      """;

  public static final String INVALID_AUTHORITY = """
      {
        "code": 13007,
        "message": "해당 기능에 대한 사용자 권한이 없습니다."
      }
      """;

  public static final String ANNOUNCEMENT_NOT_EXISTING = """
      {
        "code": 13008,
        "message": "해당 아이디의 공지사항이 존재하지 않습니다."
      }
      """;

}
