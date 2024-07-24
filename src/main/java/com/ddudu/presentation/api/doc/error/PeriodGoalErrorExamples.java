package com.ddudu.presentation.api.doc.error;

public final class PeriodGoalErrorExamples {

  public static final String PERIOD_GOAL_USER_NOT_EXISTING = """
      {
        "code": 4004,
        "message": "기간 목표의 사용자는 필수값입니다."
      }
      """;

  public static final String PERIOD_GOAL_INVALID_AUTHORITY = """
      {
        "code": 4006,
        "message": "해당 기간 목표에 대한 권한이 없습니다."
      }
      """;

}
