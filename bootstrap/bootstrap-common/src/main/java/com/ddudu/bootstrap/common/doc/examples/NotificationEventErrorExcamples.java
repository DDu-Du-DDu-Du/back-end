package com.ddudu.bootstrap.common.doc.examples;

public final class NotificationEventErrorExcamples {

  public static final String NULL_TYPE_CODE = """
      {
        "code": 10001,
        "message": "알림 유형은 필수값입니다."
      }
      """;

  public static final String NULL_RECEIVER_ID = """
      {
        "code": 10002,
        "message": "알림 수신자는 필수값입니다."
      }
      """;

  public static final String NULL_CONTEXT_ID = """
      {
        "code": 10003,
        "message": "알림 대상 컨텍스트는 필수값입니다."
      }
      """;

}
