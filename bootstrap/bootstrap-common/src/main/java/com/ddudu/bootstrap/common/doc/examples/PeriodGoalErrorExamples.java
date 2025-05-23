package com.ddudu.bootstrap.common.doc.examples;

public final class PeriodGoalErrorExamples {

  public static final String PERIOD_GOAL_CONTENTS_NOT_EXISTING = """
      {
        "code": 4001,
        "message": "기간 목표의 내용은 필수값입니다."
      }
      """;

  public static final String PERIOD_GOAL_TYPE_NOT_EXISTING = """
      {
        "code": 4002,
        "message": "기간 목표의 타입은 필수값입니다"
      }
      """;

  public static final String PERIOD_GOAL_PLAN_DATE_NOT_EXISTING = """
      {
        "code": 4003,
        "message": "기간 목표의 날짜는 필수값입니다."
      }
      """;

  public static final String PERIOD_GOAL_USER_NOT_EXISTING = """
      {
        "code": 4004,
        "message": "기간 목표의 사용자는 필수값입니다."
      }
      """;

  public static final String PERIOD_GOAL_PERIOD_GOAL_NOT_EXISTING = """
      {
        "code": 4005,
        "message": "기간 목표가 존재하지 않습니다."
      }
      """;

  public static final String PERIOD_GOAL_INVALID_AUTHORITY = """
      {
        "code": 4006,
        "message": "해당 기간 목표에 대한 권한이 없습니다."
      }
      """;

}
