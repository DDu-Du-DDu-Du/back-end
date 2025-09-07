package com.ddudu.bootstrap.common.doc.examples;

public final class DailyBriefingErrorExamples {

  public static final String NULL_USER_ID = """
      {
        "code": 11001,
        "message": "데일리 사전 요약 발송 대상 유저는 필수값입니다."
      }
      """;

  public static final String LOGIN_USER_NOT_EXISTING = """
      {
        "code": 11002,
        "message": "로그인 사용자를 찾을 수 없습니다."
      }
      """;

}
