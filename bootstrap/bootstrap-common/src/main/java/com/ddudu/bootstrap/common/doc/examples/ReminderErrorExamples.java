package com.ddudu.bootstrap.common.doc.examples;

public final class ReminderErrorExamples {

  public static final String REMINDER_NULL_TODO_VALUE = """
      {
        "code": 2102,
        "message": "투두는 필수값입니다."
      }
      """;

  public static final String REMINDER_NULL_REMINDS_AT = """
      {
        "code": 2103,
        "message": "미리알림 시간은 필수값입니다."
      }
      """;

  public static final String REMINDER_NULL_SCHEDULED_AT = """
      {
        "code": 2104,
        "message": "일정 시간은 필수값입니다."
      }
      """;

  public static final String REMINDER_INVALID_REMINDS_AT = """
      {
        "code": 2105,
        "message": "미리알림 시간은 일정 시간보다 이전이어야 합니다."
      }
      """;

  public static final String REMINDER_LOGIN_USER_NOT_EXISTING = """
      {
        "code": 2106,
        "message": "로그인 아이디가 존재하지 않습니다."
      }
      """;

  public static final String REMINDER_TODO_NOT_EXISTING = """
      {
        "code": 2107,
        "message": "할 일 아이디가 존재하지 않습니다."
      }
      """;

  public static final String REMINDER_INVALID_AUTHORITY = """
      {
        "code": 2108,
        "message": "해당 기능에 대한 사용자 권한이 없습니다."
      }
      """;

  public static final String REMINDER_ALREADY_REMINDED = """
      {
        "code": 2111,
        "message": "이미 발송된 미리알림은 취소할 수 없습니다."
      }
      """;

  private ReminderErrorExamples() {
  }

}
