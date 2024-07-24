package com.ddudu.presentation.api.doc.error;

public final class AuthErrorExamples {

  public static final String AUTH_REFRESH_NOT_ALLOWED = """
      {
        "code": 5007,
        "message": "잘못된 리프레시 토큰입니다. 갱신할 수 없습니다."
      }
      """;

  public static final String AUTH_USER_NOT_FOUND = """
      {
        "code": 5008,
        "message": "토큰을 생성할 사용자를 찾을 수 없습니다."
      }
      """;

}
