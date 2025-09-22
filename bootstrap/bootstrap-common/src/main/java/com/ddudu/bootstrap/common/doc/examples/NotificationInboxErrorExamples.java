package com.ddudu.bootstrap.common.doc.examples;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationInboxErrorExamples {

  public static final String LOGIN_USER_NOT_EXISTING = """
      {
        "code": 11007,
        "message": "로그인 사용자가 존재하지 않습니다."
      }
      """;

  public static final String INBOX_NOT_EXISTING = """
      {
        "code": 11008,
        "message": "해당 아이디의 알림 인박스가 존재하지 않습니다."
      }
      """;

  public static final String NOT_AUTHORIZED_TO_INBOX = """
      {
        "code": 11009,
        "message": "해당 인박스에 권한이 없는 사용자입니다."
      }
      """;

}

