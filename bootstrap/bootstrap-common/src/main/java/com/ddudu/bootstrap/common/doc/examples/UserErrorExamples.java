package com.ddudu.bootstrap.common.doc.examples;

public final class UserErrorExamples {

  public static final String USER_NO_TARGET_FOR_MY_INFO = """
      {
        "code": 1011,
        "message": "존재하지 않는 사용자는 내 정보를 불러올 수 없습니다."
      }
      """;

  public static final String USER_INVALID_WEEK_START_DAY = """
      {
        "code": 1012,
        "message": "주 시작 요일은 MON 또는 SUN만 입력할 수 있습니다."
      }
      """;

}
