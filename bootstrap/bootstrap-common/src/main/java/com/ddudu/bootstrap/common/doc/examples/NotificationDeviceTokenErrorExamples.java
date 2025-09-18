package com.ddudu.bootstrap.common.doc.examples;

public final class NotificationDeviceTokenErrorExamples {

  private NotificationDeviceTokenErrorExamples() {
  }

  public static final String INVALID_CHANNEL = """
      {
        "code": 12002,
        "message": "유효하지 않은 채널 유형입니다."
      }
      """;

  public static final String NULL_TOKEN = """
      {
        "code": 12003,
        "message": "디바이스 토큰 값은 필수값입니다."
      }
      """;

  public static final String EXCESSIVE_TOKEN_LENGTH = """
      {
        "code": 12004,
        "message": "디바이스 토큰의 최대 길이는 512입니다."
      }
      """;

  public static final String LOGIN_USER_NOT_EXISTING = """
      {
        "code": 12005,
        "message": "로그인 사용자가 존재하지 않습니다."
      }
      """;
}

