package com.ddudu.bootstrap.userapi.auth.doc;

public final class AuthErrorExamples {

  public static final String AUTH_INVALID_TOKEN_AUTHORITY = """
      {
        "code": 5001,
        "message": "유효하지 않은 토큰 권한입니다."
      }
      """;

  public static final String AUTH_BAD_TOKEN_CONTENT = """
      {
        "code": 5002,
        "message": "유효하지 않은 토큰 형식입니다."
      }
      """;

  public static final String AUTH_INVALID_AUTHORITY = """
      {
        "code": 5003,
        "message": "해당 기능에 대한 사용자 권한이 없습니다."
      }
      """;

  public static final String AUTH_UNABLE_TO_PARSE_USER_FAMILY_VALUE = """
      {
        "code": 5004,
        "message": "파싱할 수 없는 값입니다."
      }
      """;

  public static final String AUTH_INVALID_USER_ID_FOR_REFRESH_TOKEN = """
      {
        "code": 5005,
        "message": "리프레시 토큰의 사용자 아이디가 올바르지 않습니다."
      }
      """;

  public static final String AUTH_INVALID_REFRESH_TOKEN_FAMILY = """
      {
        "code": 5006,
        "message": "리프레시 토큰 패밀리가 올바르지 않습니다."
      }
      """;

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
