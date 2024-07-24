package com.ddudu.presentation.api.doc.error;

public final class GoalErrorExamples {

  public static final String GOAL_USER_NOT_EXISTING = """
      {
        "code": 3008,
        "message": "해당 아이디를 가진 사용자가 존재하지 않습니다."
      }
      """;

  public static final String GOAL_ID_NOT_EXISTING = """
      {
        "code": 3004,
        "message": "해당 아이디를 가진 목표가 존재하지 않습니다."
      }
      """;

  public static final String GOAL_INVALID_AUTHORITY = """
      {
        "code": 3009,
        "message": "해당 기능에 대한 사용자 권한이 없습니다."
      }
      """;

}
